package com.vn.advent.year_2022.solutions;

import com.vn.advent.Solution;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day7 implements Solution {

	private static final Pattern COMMAND_PATTERN = Pattern.compile("^\\$\\s(((cd)\\s(\\.{2}|\\w+|\\/))|ls)");
	private static final Pattern OUTPUT_PATTERN = Pattern.compile("(^((\\d+)\\s(\\w+(\\.\\w{3}){0,1}$))|^(dir)\\s(\\w+))");
	private static final TreeNode<File> ROOT = new TreeNode<>(File.ROOT_FOLDER);
	private static TreeNode<File> PWD = ROOT;

	private static final Long MIN_UNUSED_DISK_SPACE_NEEDED = 30000000l;
	private static final Long TOTAL_DISK_SPACE = 70000000l;

	public static void main(String[] args) {
		Solution solution = new Day7();
		System.out.println(solution.run());
	}

	public String partOne(Stream<String> lines) {
		File.MAP_OF_FILE_OBJ_AND_NODE.put(File.ROOT_FOLDER, ROOT);
		lines.forEach(this::processLine);
		return String.valueOf(File.allFoldersIn(ROOT.data)
				.map(File::folderSize)
				.filter(folderSize -> folderSize<=100000)
				.reduce(Long::sum)
				.get());
	}


	public String partTwo(Stream<String> lines) {
		final long unusedDiskSpace = TOTAL_DISK_SPACE - File.folderSize(File.ROOT_FOLDER);
		return String.valueOf(File.allFoldersIn(ROOT.data)
				.map(File::folderSize)
				.map(folderSize -> unusedDiskSpace + folderSize)
				.filter(newUnusedDiskSpace -> newUnusedDiskSpace >= MIN_UNUSED_DISK_SPACE_NEEDED)
				.min(Long::compare)
				.get() - unusedDiskSpace);
	}



	private void processLine(String line) {

		COMMAND_PATTERN.matcher(line)
				.results()
				.filter(matchResult -> matchResult.group(3) != null)
				.filter(matchResult -> matchResult.group(3).equals("cd"))
				.forEach(matchResult -> {
						final String arg = matchResult.group(4);
						if(arg.equals("/"))
							PWD = ROOT;
						else if (arg.equals("..") && PWD != ROOT) {
							PWD = PWD.parent;
						} else {
							PWD = File.MAP_OF_FILE_OBJ_AND_NODE.get(new File(arg, PWD, true, 0l));
						}
				});

		OUTPUT_PATTERN.matcher(line)
				.results()
				.forEach(matchResult -> {
					if(matchResult.group(6) != null && matchResult.group(6).equals("dir")) {
						final String directoryName = matchResult.group(7);
						final File directory = new File(directoryName, PWD, true, 0l);
						final TreeNode<File> directoryNode = new TreeNode<>(directory);
						directoryNode.parent = PWD;
						PWD.children.add(directoryNode);
						File.MAP_OF_FILE_OBJ_AND_NODE.put(directory, directoryNode);
					} else {
						final Long fileSize = Long.valueOf(matchResult.group(3));
						final String fileName = matchResult.group(4);
						final TreeNode<File> fileTreeNode = new TreeNode<>(new File(fileName, PWD, false, fileSize));
						fileTreeNode.parent = PWD;
						PWD.children.add(fileTreeNode);
					}
				});
	}

	private record File(String name, TreeNode<File> parent, Boolean isDirectory, Long size) {

		public static final File ROOT_FOLDER = new File("/", null,true, 0l);
		public static final Map<File, TreeNode<File>> MAP_OF_FILE_OBJ_AND_NODE = new HashMap<>();
		static Stream<File> allFilesIn(File folder) {
			final Set<TreeNode<File>> children = MAP_OF_FILE_OBJ_AND_NODE.get(folder).children;
			final Stream<File> descendants = children.stream()
					.filter(node -> node.data.isDirectory)
					.flatMap(node -> File.allFilesIn(node.data));
			return Stream.concat(descendants,
					children.stream().filter(node -> !node.data.isDirectory).map(node -> node.data));
		}

		static Long folderSize(File folder) {
			return allFilesIn(folder)
					.mapToLong(File::size)
					.sum();
		}

		static Stream<File> allFoldersIn(File folder) {
			final Set<TreeNode<File>> children = MAP_OF_FILE_OBJ_AND_NODE.get(folder).children;
			final Stream<File> descendants = children.stream()
					.filter(node -> node.data.isDirectory)
					.flatMap(node -> File.allFoldersIn(node.data));
			return Stream.concat(descendants,
					children.stream().filter(node -> node.data.isDirectory).map(node -> node.data));
		}
	}

	public static class TreeNode<T> {
		private final T data;
		private Set<TreeNode<T>> children = new HashSet<>();
		private TreeNode<T> parent;
		TreeNode(T data) {
			this.data = data;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			TreeNode<?> treeNode = (TreeNode<?>) o;
			return data.equals(treeNode.data) && Objects.equals(parent, treeNode.parent);
		}

		@Override
		public int hashCode() {
			return Objects.hash(data, parent);
		}

		@Override
		public String toString() {
			return data.toString();
		}
	}

	@Override
	public String getInputFileName() {
		return "2022/input_7";
	}
}
