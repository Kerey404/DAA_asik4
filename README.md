---
#Assignment 4 — Smart City / Smart Campus Scheduling

#Course: Design and Analysis of Algorithms (DAA)
#Student: Bakytzhan Kassymgali 
#Group: SE-2421

---
Goal

The goal of this assignment is to consolidate two course topics in one practical scenario:
Strongly Connected Components (SCC) & Topological Ordering
and Shortest Paths in Directed Acyclic Graphs (DAGs).

The project models Smart City / Smart Campus scheduling —
analyzing dependencies between maintenance and analytics tasks.

---

Implemented Algorithms
| Package           | Class                          | Algorithm                      | Description                                                    |
| ----------------- | ------------------------------ | ------------------------------ | -------------------------------------------------------------- |
| `graph.scc`       | `SCCFinder.java`               | **Tarjan’s Algorithm**         | Detects strongly connected components (O(V + E))               |
| `graph`           | `Graph.buildCondensation(...)` | —                              | Builds condensation DAG of components                          |
| `graph.topo`      | `TopologicalSorter.java`       | **Kahn’s Algorithm**           | Computes topological order of DAG                              |
| `graph.dagsp`     | `DAGShortestPath.java`         | **Dynamic Programming on DAG** | Calculates single-source shortest and longest (critical) paths |
| `graph.Main.java` | —                              | Execution Pipeline             | Loads JSON → SCC → Condensation → Topo Sort → SP/LP → Output   |

---

Datasets
Nine datasets were generated to test algorithms on different structures and densities.
| Category   | Nodes | Description                            | Variants |
| ---------- | ----- | -------------------------------------- | -------- |
| **Small**  | 6–10  | Simple DAGs or few cycles              | 3        |
| **Medium** | 10–20 | Mixed cyclic/acyclic with several SCCs | 3        |
| **Large**  | 20–50 | Dense graphs for timing tests          | 3        |
[data](https://github.com/Kerey404/DAA_asik4/tree/main/data)

---
Metrics and Instrumentation
| Algorithm            | Metrics Counted                          |
| -------------------- | ---------------------------------------- |
| **SCC (Tarjan)**     | DFS calls, edges visited, execution time |
| **Topo Sort (Kahn)** | Push/pop operations, execution time      |
| **DAG SP/LP**        | Relaxations count, execution time        |

---
Tests
JUnit tests are implemented under src/test/java/graph/:
SCCFinderTest.java — detects SCC count and correctness.
TopologicalSorterTest.java — verifies valid topological order.
DAGShortestPathTest.java — checks correctness of shortest/longest path DP.

---
Results Summary
| Dataset  | Nodes | Edges | SCCs | Topo Time (ms) | Critical Path |
| -------- | ----- | ----- | ---- | -------------- | ------------- |
| small_1  | 8     | 18    | 3    | 0.063          | 3.0           |
| medium_2 | 17    | 37    | 6    | 0.071          | 11.0          |
| large_3  | 37    | 92    | 10   | 0.077          | 24.0          |

---
Report & Analysis

A detailed analytical report includes:
Data summary (nodes, edges, density, cyclicity)
Per-task metrics and timing results
Bottleneck analysis: effect of graph structure, SCC size, and density
Practical recommendations for algorithm use
File:[Analysis](https://github.com/Kerey404/DAA_asik4/blob/main/%D0%B0%D0%BD%D0%B0%D0%BB%D0%B8%D1%82%D0%B8%D0%BA%D0%B0_%D0%B4%D0%B0%D0%B0.docx)
---
Conclusions

Tarjan (SCCFinder) — best for dense cyclic networks.
Kahn Topological Sort — optimal for task scheduling and dependency analysis.
DAG Shortest/Longest Path — ideal for workflow or critical path planning.
Combination of these methods yields optimal graph analysis for real Smart City / Smart Campus task scheduling.
