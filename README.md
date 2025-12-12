#SIMPLE CHAT APP
A robust, client-server chat application built using Java TCP Sockets. The server is multi-threaded for scalability, and the client features a custom Dark Mode UI.

Key Features
Multi-Threaded Server: Uses a thread pool (ExecutorService) to handle numerous concurrent connections efficiently.
Chat History: The server stores recent messages in a thread-safe list and automatically sends the history to new users upon connection.
Non-Blocking UI: The client runs its network listener in a separate thread, ensuring the Dark Mode Swing UI remains responsive.
Reliable Transport: Uses the TCP protocol for guaranteed message delivery.

🛠️ How to Run
Compile:
Bash
javac ChatServer.java ChatClient.java
Start Server: (In Terminal 1)
Bash
java ChatServer
Start Clients: (In Terminal 2, 3, etc.)
Bash
java ChatClient
