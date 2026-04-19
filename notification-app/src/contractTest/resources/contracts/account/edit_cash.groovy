package contracts.account

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description 'Post edit cash account'
    name 'edit_cash'

    request {
        method POST()
        url '/notification/cash'
        headers {
            contentType(applicationJson())
            header 'Authorization', value(
                    consumer(regex('Bearer\\s+.+')),   // для консьюмера (WireMock): любой Bearer-токен
                    producer('Bearer test-token')  // для провайдера (MockMvc-тест): ровно этот токен
            )
        }
        body(
                username: 'user1',
                name: 'name',
                typeAction: 'PUT',
                actionValue: 10,
                afterValue: 10
        )
    }

    response {
        status OK()
    }
}