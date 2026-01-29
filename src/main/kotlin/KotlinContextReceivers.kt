package org.example

data class Job(val id: JobId, val company: Company, val role: Role, val salary: Salary)
//{
//    fun toJson(): String = TODO()
//}

@JvmInline
value class JobId(val value: Long)

@JvmInline
value class Company(val name: String)

@JvmInline
value class Role(val name: String)

@JvmInline
value class Salary(val value: Double)


val JOBS_DATABASE: Map<JobId, Job> = mapOf(
    JobId(1) to Job(
        JobId(1),
        Company("Apple, Inc"),
        Role("Software Engineer"),
        Salary(25.0),
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

interface JsonScope<A> { // dispatcher receiver
    fun A.toJson(): String
}

val jobJsonScope = object : JsonScope<Job> {
    override fun Job.toJson(): String =
        """
        {
            "id": ${id.value},
            "company": "${company.name}",
            "role": "${role.name}",
            "salary": ${salary.value},
    """".trimIndent()
}

//fun printAsJson(jobs: List<Job>) =
//    jobs.joinToString(separator = ", ", prefix = "[", postfix = "]") {
//        it.toJson()
//    }

interface Logger {
    fun info(message: String)
}

val consoleLogger = object : Logger {
    override fun info(message: String) {
        println("[INFO] $message")
    }
}

context(scope: JsonScope<A>, logger: Logger)
fun <A> printAsJson(things: List<A>): String {
    with(scope) {
        logger.info("serializing $things as Json")
        return things.joinToString(separator = ", ", prefix = "[", postfix = "]") {
            it.toJson()
        }
    }
}

//fun <A> JsonScope<A>.printAsJson(things: List<A>) : String =
//    things.joinToString(separator = ", ", prefix = "[", postfix = "]") {
//        it.toJson()
//    }


interface Jobs {
    context (scope: Logger)
    fun findJobById(id: JobId): Job?
}

class LiveJobs : Jobs {
    context (scope: Logger)
    override fun findJobById(id: JobId): Job? {
        scope.info("findJobById: $id")
        return JOBS_DATABASE[id]
    }
}

class JobController {
    context(json: JsonScope<Job>, logger: Logger, jobs: Jobs)
    fun jsonById(id: String): String {
        logger.info("Searching for $id to serialize")
        return jobs.findJobById(JobId(id.toLong()))?.let { job ->
            logger.info("Found job $id")
            with(json) {
                job.toJson()
            }
        } ?: "Not Found"
    }
}

fun main() {
    with(jobJsonScope) {
        with(consoleLogger) {
            println(printAsJson(JOBS_DATABASE.values.toList()))
        }
    }
}