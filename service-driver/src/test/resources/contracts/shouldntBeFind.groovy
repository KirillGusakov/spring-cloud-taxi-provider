package contracts

org.springframework.cloud.contract.spec.Contract.make {
    description "Should return an exception"

    request {
        method GET()
        url '/api/v1/drivers/1001'
    }

    response {
        status NOT_FOUND()
        headers {
            contentType(applicationJson())
        }
        body(
                message: "Driver with id = 1001 not found"
        )
    }
}