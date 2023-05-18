package com.example.agileandroidalpha

enum class Status(val string: String) {
    Cancelled("Cancelled"),
    Unknown("Unknown"),
    Reopened("Re-opened"),
    Open("Open"),
    TO_DO("TO DO"),
    MI("Missing Information"),
    Stalled("Stalled"),
    NS("Not Started"),
    RC("Requires Consultation"),
    InProgress("In Progress"),
    Fixing("Fixing"),
    Reviewing("Reviewing"),
    Skipped("Skipped"),
    Workaround("Workaround Available"),
    Done("Done"),
    AwaitingReview("Awaiting Review"),
    Reviewed("Reviewed"),
    AwaitingApproval("Awaiting Approval"),
    ReqAttention("Requires Attention"),
    NotApproved("Not Approved"),
    Declined("Declined"),
    Scrapped("Scrapped"),
    Approved("Approved"),
    Closed("Closed"),
    Archived("Archived")
}

enum class Priority(val string: String, val value: Int) {

    Donut("Donut", -1),
    None ("None", 0),
    Trivial("Trivial", 1),
    Lowest ("Lowest", 2),
    Low ("Low", 3),
    Medium ("Medium", 4),
    High ("High", 5),
    Highest ("Highest", 6),
    Critical ("Critical", 7),
    Urgent ("Urgent", 8),
    Showstopper ("Showstopper", 9),
    Catastrophic ("Catastrophic", 10);


    companion object {
        fun iToS(i: Int) = Priority.values()[i].string
        fun sToI(s: String) = Priority.values()
        fun pair(i: Int, s: String) = Priority to Pair(i, s)
    }

}