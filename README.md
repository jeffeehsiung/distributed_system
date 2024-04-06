# Cloud-Based Deployment with IaaS on Azure

This project explores deploying and testing developed remote services including RMI, SOAP, and REST on Azure Virtual Machines. Through this exercise, we evaluate the performance, response time, and throughput of these services deployed across global data centers.

## Team Members
order by first name

- Member 1: [Geng Luo](#)
- Member 2: [Ivan Deyanov Ivanov](#)
- Member 3: [Jeffee Hsiung](https://github.com/jeffeehsiung)
- Member 4: [Jiaao Liu](https://github.com/Benliu2477)

## Introduction

In this practical session, we leveraged the Azure Education program to deploy our remote services on virtual machines located in various regions around the world including West Europe, US East Coast, US West Coast, and Japan East. The goal was to assess the performance of these services under different load conditions and from various client locations worldwide.

## Azure Education Account Activation

Each KU Leuven student has access to $100 credits on Azure, with the ability to run certain services for free under the Azure Education program. More details can be found [here](https://icts.kuleuven.be/sc/english/software/microsoft-adtt).

## Services Deployed

- **RMI (Remote Method Invocation) Service**: Deployed to manage remote object invocation.
- **SOAP (Simple Object Access Protocol) Service**: A protocol for exchanging structured information in web service implementation.
- **REST (Representational State Transfer) Service**: Utilized for creating scalable web services, following HATEOAS principles for listing meals.

## Deployment Regions

The **server** services were deployed in the following Azure regions:

- Australia East (main load testing server)
- US East Coast
- US East Coast
- South Africa North

The **client** were deployed in the following Azure regions:
- North Europe
- Canada Central
- Japan North
- Australia East

## Testing Strategy

Our testing strategy involved creating concurrent clients that generate load on the deployed services from different global locations. We experimented with varying the number of parallel clients and requests per second to assess:

- Response Time
- Throughput
- CPU Performance under different loads

## Performance Testing and Results

A detailed report of our testing strategy, including the scenarios tested, is included. We focused on statistical results rather than single-request outcomes to better simulate real-world usage. Key findings include:

- Response times from different client and service locations
- Throughput rates with increasing load
- CPU load and memory usage during the tests

[Link to Detailed Report](Team6_DistributedSystem_Azure_SOAP_REST_Test.pdf)

### Statistical Results of RESTful Service

| **#User** | **Case**         | **Requests** | **Error Count** | **Error Percentage** | **Response Time(s)** | **Throughput** |
|-----------|------------------|--------------|-----------------|----------------------|----------------------|----------------|
| 100       | baseline         | 3965718      | 0               | 0%                   | 0.03                 | 4131           |
| 1000      | North Europe     | 780674       | 0               | 0%                   | 1.27                 | 813            |
| 1000      | Canada           | 867135       | 0               | 0%                   | 1.14                 | 903            |
| 1000      | Japan            | 899296       | 0               | 0%                   | 1.12                 | 937            |
| 1000      | Aggregate        | 2547105      | 0               | 0%                   | 1.17                 | 2653           |
| 2000      | North Europe     | 921374       | 730             | 0.08%                | 1.89                 | 960            |
| 2000      | Canada           | 1030305      | 729             | 0.07%                | 1.65                 | 1073           |
| 2000      | Japan            | 1104702      | 410             | 0.04%                | 1.74                 | 1151           |
| 2000      | Aggregate        | 3056381      | 1869            | 0.06%                | 1.76                 | 1061           |
| 5000      | North Europe     | 487280       | 17469           | 3.59%                | 2.97                 | 508            |
| 5000      | Canada           | 834235       | 13068           | 1.57%                | 2.56                 | 869            |
| 5000      | Japan            | 1228344      | 8791            | 0.72%                | 2.48                 | 1280           |
| 5000      | Aggregate        | 2549859      | 39328           | 1.54%                | 2.67                 | 885            |

### Statistical Results of SOAP Service

| **#User** | **Case**         | **Requests** | **Error Count** | **Error Percentage** | **Response Time(s)** | **Throughput** |
|-----------|------------------|--------------|-----------------|----------------------|----------------------|----------------|
| 100       | baseline         | 1030784      | 0               | 0.00%                | 0.01                 | 1073.73        |
| 1000      | North Europe     | 348424       | 0               | 0.00%                | 2.41                 | 362.94         |
| 1000      | Canada           | 404637       | 0               | 0.00%                | 2.16                 | 421.5          |
| 1000      | Japan            | 415044       | 0               | 0.00%                | 2.06                 | 432.34         |
| 1000      | Aggregate        | 1168105      | 0               | 0.00%                | 2.21                 | 405.59         |
| 2000      | North Europe     | 235447       | 268             | 0.11%                | 6.94                 | 245.26         |
| 2000      | Canada           | 386606       | 127             | 0.03%                | 5.53                 | 402.72         |
| 2000      | Japan            | 456811       | 67              | 0.01%                | 4.9                  | 475.85         |
| 2000      | Aggregate        | 1078864      | 462             | 0.04%                | 5.79                 | 375.61         |
| 5000      | North Europe     | 157541       | 18507           | 11.75%               | 94.39                | 164.11         |
| 5000      | Canada           | 266181       | 14201           | 5.34%                | 25.97                | 277.27         |
| 5000      | Japan            | 425360       | 8530            | 2.01%                | 9.67                 | 443.08         |
| 5000      | Aggregate        | 849082       | 41238           | 4.86%                | 43.34                | 294.82         |

## Conclusion

- Our study presents an overall evaluation of SOAP and RESTful web services across various geographical locations and under different user load scenarios. The empirical analysis distinctly emphasizes the influence of geographical distance on response times, with services exhibiting varying latency degrees based on the proximity between client and server nodes. Regions closer to the server nodes experienced reduced response times, underscoring the critical need for strategic server placement to enhance performance globally.

- Both the response time and throughput analysis highlights the challenges regarding scalability for both SOAP and RESTful services under elevated user loads. However, the impact of these challenges varied between the two service types. RESTful services demonstrated a relative resilience to load and geographical influences, maintaining more consistent latencies under comparable conditions. Conversely, SOAP services were significantly affected, especially in scenarios involving users from distant regions, indicating a pronounced sensitivity to load increases and geographical dispersion.

- At last, the study identified a direct correlation between elevated error rates, increased CPU utilization, and user load increments, pointing to the operational boundaries of both SOAP and RESTful architectures. This correlation underscores the imperative for robust scalability solutions to mitigate system stress and maintain service reliability under high demand.

## Acknowledgments

We would like to thank our lab coaches for their guidance throughout this project:

- Bert Lagaisse
- Toon Dehaene
- Tianyu Li