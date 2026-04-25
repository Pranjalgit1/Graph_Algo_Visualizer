# Graph Algorithm Visualizer 🕸️✨

A modern, highly interactive JavaFX application designed to visualize and compare fundamental graph algorithms in real-time. Built with a sleek, dark-themed SaaS-style dashboard, this tool is perfect for students, educators, and developers who want to intuitively understand how graph algorithms traverse nodes, calculate shortest paths, and construct minimum spanning trees.
## 🖼️ Preview

### 🎯 Main Interface
<img width="1920" height="1200" alt="Screenshot (133)" src="https://github.com/user-attachments/assets/980ee5aa-20ab-4a18-8042-c162410ded6c" />


### ⚡ Algorithm Visualization (BFS)
<img width="1920" height="1200" alt="Screenshot (134)" src="https://github.com/user-attachments/assets/4c5665c1-59d4-4dfb-9663-a8e24d49bcea" />


### 🔄 Compare Mode
<img width="1920" height="1200" alt="Screenshot (135)" src="https://github.com/user-attachments/assets/5e05ba20-395a-4496-be7e-0127c7ccb683" />

## 🚀 Features

- **Interactive Graph Canvas**: Drag and drop nodes to reposition them, double-click edge weights to edit them inline, and hover over elements for tooltips.
- **Single & Compare Modes**: Run a single algorithm to study its steps in detail, or run two algorithms side-by-side to compare their execution speed and traversal paths.
- **Real-Time Step Animation**: Watch algorithms execute step-by-step with synchronized pseudocode highlighting and traversal logs.
- **Extensive Algorithm Suite**:
  - **Pathfinding**: BFS, DFS, Dijkstra's, Bellman-Ford, Floyd-Warshall
  - **Minimum Spanning Tree (MST)**: Kruskal's, Prim's
  - **Advanced**: Topological Sort, Traveling Salesperson Problem (TSP)
- **Pre-loaded Sample Graphs**: Quickly load common graph topologies like DAGs, Complete Graphs (K5), Trees, and graphs with negative edges.
- **Detailed Execution Analytics**: View total runtime, algorithmic time complexity, and step-by-step traversal outputs.

## 🛠️ Technology Stack

- **Java**: Core logic and algorithm implementations (JDK 21+)
- **JavaFX**: High-performance UI rendering and animations

## ⚙️ Project Structure

- `src/ui/`: Contains all JavaFX UI components (`MainApp`, `GraphInputScreen`, `GraphRenderer`, `StepAnimator`).
- `src/graph/`: Core data structures representing the graph, nodes, and edges (`Graph`, `VisualNode`, `VisualEdge`).
- `src/algorithms/`: Individual implementations for each graph algorithm.
- `src/step/`: Logic for recording and emitting algorithmic steps (`Step`, `StepType`) to synchronize with the UI.

## 🏃‍♂️ How to Run

1. Ensure you have **Java JDK 21+** installed.
2. Download the **JavaFX SDK** (version 21 or later) for your operating system.
3. Add the JavaFX `lib` folder to your module path when compiling and running.

**Example Compilation:**
```bash
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml,javafx.graphics -d bin src/**/*.java
```

**Example Execution:**
```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp bin ui.MainApp
```

*(Note: Replace `/path/to/javafx-sdk/lib` with the actual path to your downloaded JavaFX SDK library folder).*

## 🤝 Contributing

Contributions are welcome! If you'd like to add a new algorithm or improve the UI:
1. Fork the repository.
2. Create a new feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.


