
ReqestCycle:

    public getQuerystringParameters() {
        PROBLEM: querystring parameters are named, not ordered. This cannot work the same way as path parameters.
                The handler can be expected to ask for all known querystring parameters at once. Some ad-hoc logic
            passing names in an array or something? Or just use a record: The record class defines fields and their
        types, and this method returns an instance of the record class. That duplicates the JSON parser logic to
                a certain extent, but at least it's a nice solution.
    }
