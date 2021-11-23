# safety-edge-service
Een API waarmee een autokeuringsproces gevolgd kan worden.
Het bevat 2 backend-microservices, één voor het beheren van
informatie over de auto en de andere om voertuiginspectiegegevens
te beheren.
![img_1.png](img_1.png)

### car-info-service
https://github.com/AlbertBaffour/car-info-service

### inspection-service
https://github.com/AlbertBaffour/car-inspection-service

## Postman Requests
#### CARS
GET Request - Cars (all)
![PostmanImg/Cars/GetCars.png](PostmanImg/Cars/GetCars.png)

GET Request - Cars By LicencePlate
![PostmanImg/Cars/GetCarsByLicencePlate.PNG](PostmanImg/Cars/GetCarsByLicencePlate.PNG)

GET Request - Cars By Merk
![PostmanImg/Cars/GetCarsByMerk.PNG](PostmanImg/Cars/GetCarsByMerk.PNG)

GET Request - Cars By Portier
![PostmanImg/Cars/GetCarsByPortier.PNG](PostmanImg/Cars/GetCarsByPortier.PNG)

POST Request - Add Car
![PostmanImg/Cars/PostCar.PNG](PostmanImg/Cars/PostCar.PNG)

PUT Request - Update Car
![PostmanImg/Cars/PutCar.PNG](PostmanImg/Cars/PutCar.PNG)

DELETE Request - Delete car
![PostmanImg/Cars/DeleteCar.PNG](PostmanImg/Cars/DeleteCar.PNG)

#### CAR INSPECTION

GET Request - Inpections (all)
![PostmanImg/Inspections/GetInspections.PNG](PostmanImg/Inspections/GetInspections.PNG)

GET Request - Inspections By LicensePlate
![PostmanImg/Inspections/GetInspectionByLicensePlate.PNG](PostmanImg/Inspections/GetInspectionByLicensePlate.PNG)

GET Request - Inspections By LicensePlate and Date
![PostmanImg/Inspections/GetInspectionByLicensePlateAndDate.PNG](PostmanImg/Inspections/GetInspectionByLicensePlateAndDate.PNG)

POST Request - Add Inspection
![PostmanImg/Inspections/PostInspection.PNG](PostmanImg/Inspections/PostInspection.PNG)

PUT Request - Update Inspection
![PostmanImg/Inspections/PutInspection.PNG](PostmanImg/Inspections/PutInspection.PNG)

DELETE Request - Delete Inspection
![PostmanImg/Inspections/DeleteInspection.PNG](PostmanImg/Inspections/DeleteInspection.PNG)

## Swagger UI
![img.png](img.png)



