package org.example.error.rock

import arrow.core.*
import arrow.core.raise.context.bind
import arrow.core.raise.context.ensureNotNull
import arrow.core.raise.either
import arrow.core.raise.result

data class Job(val id: JobId, val company: Company, val role: Role, val salary: Salary)

@JvmInline
value class JobId(val value: Long)

@JvmInline
value class Company(val name: String)

@JvmInline
value class Role(val name: String)

@JvmInline
value class Salary(val value: Double) {
    operator fun compareTo(other: Salary): Int = value.compareTo(other.value)
}

interface Jobs {
    fun findById(id: JobId): Result<Job?>
    fun findAll(): Result<List<Job>>
}

class LiveJobs : Jobs {
    override fun findById(id: JobId): Result<Job?> =
        runCatching {
            JOBS_DATABASE[id]
        }

    override fun findAll(): Result<List<Job>> {
        return Result.success(JOBS_DATABASE.values.toList())
    }
//        try {
//        Result.success(JOBS_DATABASE[id])
//    } catch (e: Exception) {
//        Result.failure(e)
//    }

}

class JobService(private val jobs: Jobs, val currencyConverter: CurrencyConverter) {
    fun maybePrintJob(jobId: JobId) {
        val maybeJob: Result<Job?> = jobs.findById(jobId)
        if (maybeJob.isSuccess) {
            maybeJob.getOrNull()?.apply { print("Job found: $this") } ?: run {
                println("Job not found")
            }
        } else {
            println("Something went wrong: ${maybeJob.exceptionOrNull()?.message}")
        }
    }

    fun getSalaryInEur(jobId: JobId): Result<Double> =
        jobs.findById(jobId)
            .map { it?.salary }
            .mapCatching { currencyConverter.convertUsdToEur(it?.value) }

    fun getSalaryGapVsMaxNonIdiomatic(jobId: JobId): Result<Double> = runCatching {
        val maybeJob: Job? = jobs.findById(jobId).getOrThrow()
        val jobSalary = maybeJob?.salary ?: Salary(0.0)
        val jobList = jobs.findAll().getOrThrow()
        val maxSalary = jobList.maxSalary().getOrThrow()
        println("maxSalary: ${maxSalary.value} ")
        maxSalary.value - jobSalary.value
    }

    fun getSalaryGapVsMaxIdiomatic(jobId: JobId): Result<Double> =
        jobs.findById(jobId).flatMap { maybeJob ->  // job? -> Result
            val salary = maybeJob?.salary ?: Salary(0.0)
            jobs.findAll().flatMap { jobList ->
                jobList.maxSalary().map { maxSalary ->
                    maxSalary.value - salary.value
                }
            }
        }

    fun getSalaryGapVsMaxArrow(jobId: JobId): Either<Throwable, Double> = either {
        // Converte Result para Either e faz bind
        val maybeJob: Job? = jobs.findById(jobId).toEither { it }.bind()

        // Garante que o job não é null
        ensureNotNull(maybeJob) { NoSuchElementException("Job not found") }

        val jobSalary = maybeJob.salary
        val jobList = jobs.findAll().toEither { it }.bind()
        val maxSalary = jobList.maxSalary().toEither { it }.bind()

        maxSalary.value - jobSalary.value
    }

    fun <T> Result<T>.toEither(transform: (Throwable) -> Throwable = { it }): Either<Throwable, T> =
        fold(
            { Either.Right(it) },
            { Either.Left(transform(it)) }
        )

    fun getSalaryGapVsMaxArrowAsResult(jobId: JobId): Result<Double> =
        getSalaryGapVsMaxArrow(jobId).fold(
            { error -> Result.failure(error) },
            { value -> Result.success(value) }
        )
}

class CurrencyConverter {
    fun convertUsdToEur(amount: Double?): Double {
        return if (amount != null && amount >= 0.0) amount * 0.91 else throw IllegalArgumentException("Amount must be present and positive")
    }
}

fun List<Job>.maxSalary(): Result<Salary> = runCatching {
    if (this.isEmpty()) {
        throw NoSuchElementException("No jobs present")
    } else {
        this.maxBy { it.salary.value }.salary
    }
}

val appleJobResult: Result<Job> = Result.success(
    Job(
        JobId(2),
        Company("Microsoft"),
        Role("Software Engineer"),
        Salary(30.0),
    )
)

val notFoundJob: Result<Job> = Result.failure(NoSuchElementException("Job not found"))

fun <T> T.toResult(): Result<T> =
    if (this is Throwable) Result.failure(this) else Result.success(this)

val aResult = Job(
    JobId(2),
    Company("Microsoft"),
    Role("Software Engineer"),
    Salary(30.0),
).toResult()

val JOBS_DATABASE: Map<JobId, Job> = mapOf(
    JobId(1) to Job(
        JobId(1),
        Company("Apple, Inc"),
        Role("Software Engineer"),
        Salary(80.0),
    ),
    JobId(2) to Job(
        JobId(2),
        Company("Microsoft"),
        Role("Software Engineer"),
        Salary(30.0),
    ),
    JobId(3) to Job(
        JobId(3),
        Company("Google"),
        Role("Software Engineer"),
        Salary(50.0),
    )
)

val appleJobSalary = appleJobResult.map { it.salary }
val appleJobSalaryCatching = appleJobResult.mapCatching { it.salary }

fun main(array: Array<String>) {
    val jobs = LiveJobs()
    val currencyConverter = CurrencyConverter()
    val jobService = JobService(jobs, currencyConverter)
//    jobService.maybePrintJob(JobId(2))
//    jobService.maybePrintJob(JobId(42))

    val maybeSalary = jobService.getSalaryInEur(JobId(42))

    // recover
    val recovered = maybeSalary.recover {
        when (it) {
            is IllegalStateException -> println("Job not found Amount must be positive")
            else -> println("Some other error occurred: ${it.message}")
        }

        0.0
    }

    // fold
    val finalStatement = maybeSalary.fold(
        {
            "The salary of the job is $it"
        },
        {
            when (it) {
                is IllegalStateException -> println("Job not found Amount must be positive")
                else -> println("Some other error occurred: ${it.message}")
            }
            0.0
        }
    )
    val gabSalary = jobService.getSalaryGapVsMaxNonIdiomatic(JobId(2))
    println("gabSalary: $gabSalary")

    println(recovered)
    println(finalStatement)
}