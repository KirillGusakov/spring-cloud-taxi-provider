package contracts

org.springframework.cloud.contract.spec.Contract.make {
    description "Should return passenger details by ID"

    request {
        method GET()
        url '/api/v1/passengers/1'
    }

    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                firstName: "John",
                lastName: "Doe",
                email: "john.doe@example.com",
                phoneNumber: "+1234567890",
                isDeleted: false
        )
    }
}


