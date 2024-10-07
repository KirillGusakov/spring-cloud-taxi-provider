package contracts

org.springframework.cloud.contract.spec.Contract.make {
    description "Should return exception message"

    request {
        method GET()
        url '/api/v1/passengers/1001'
    }

    response {
        status NOT_FOUND()
        headers {
            contentType(applicationJson())
        }
        body(
                message: "Passenger with id = 1001 not found"
        )
    }
}
