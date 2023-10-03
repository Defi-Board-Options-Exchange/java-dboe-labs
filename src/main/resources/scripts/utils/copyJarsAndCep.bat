xcopy /y C:\Users\truon\works\trading-lab\trading-server\build\libs\trading-server-fat.jar C:\Users\truon\works\prod
xcopy /y C:\Users\truon\works\trading-lab\cpmc-server\build\libs\cpmc-server-fat.jar C:\Users\truon\works\prod
xcopy /y C:\Users\truon\works\trading-lab\admin-server\build\libs\admin-server-fat.jar C:\Users\truon\works\prod
xcopy /y C:\Users\truon\works\trading-lab\ib\build\libs\ib-fat.jar C:\Users\truon\works\prod
xcopy /y C:\Users\truon\works\trading-lab\binance-client\build\libs\binance-client-fat.jar C:\Users\truon\works\prod
rmdir C:\Users\truon\works\prod\cep\esper\1.0 /s
xcopy /y /s /e C:\Users\truon\works\trading-lab\cep\src\main\resources\esper C:\Users\truon\works\prod\cep\esper\1.0