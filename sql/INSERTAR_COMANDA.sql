INSERT INTO [dbo].[Comandas]
           ([nropos]
           ,[centroEmisor]
           ,[nroDocumento]
           ,[fecdocumento]
           ,[fecproceso]
           ,[turno]
           ,[jsonComanda]
           ,[estado]
           ,[fecPendiente]
           ,[fecAtencion]
           ,[fecPorEntregar]
           ,[fecEntregado])
     VALUES
           ('TIENDA_05 '
           ,'035'
           ,'511000001070'
           ,'2012-06-18T10:34:09'
           ,'2012-06-18T10:34:09'
           ,2
           ,'{
  "centroemisor": "035",
  "nrodocumento": "511000001070",
  "fecdocumento": "18/07/2023 10:45:00 AM",
  "comentario": "EMPACAR POR SEPARADO",
  "Items": [{
    "ITEM": "1",
    "CDARTICULO": "00011194",
    "DSARTICULO": "MEAT LOVERS BURGER",
    "CANTIDAD": "1.00",
    "TIPOPAN": "",
    "COMENTARIOS": [{
      "COMENT": "PARA LLEVAR"
    }, {
      "COMENT": "SIN SALSA DE AJO"
    }]
  }, {
    "ITEM": "2",
    "CDARTICULO": "00013366",
    "DSARTICULO": "SW JAMON Y QUESO",
    "CANTIDAD": "1.00",
    "TIPOPAN": "",
    "COMENTARIOS": [{
      "COMENT": "PARA LLEVAR"
    }, {
      "COMENT": "SIN SALSA DE TOMATE"
    }, {
      "COMENT": "SIN MOSTAZA"
    }]
  }, {
    "ITEM": "3",
    "CDARTICULO": "00015640",
    "DSARTICULO": "SW TROPICAL CHICKEN",
    "CANTIDAD": "1.00",
    "TIPOPAN": "",
    "COMENTARIOS": [{
      "COMENT": "PARA LLEVAR"
    }, {
      "COMENT": "SIN SALSA DE TOMATE"
    }, {
      "COMENT": "SIN MOSTAZA"
    }, {
      "COMENT": "SIN CEBOLLA"
    }, {
      "COMENT": "SIN VEGETALES"
    }]
  }]
}'
           ,'1'
           ,'2012-06-18T10:34:09'
           ,NULL
           ,NULL
           ,NULL)
GO
