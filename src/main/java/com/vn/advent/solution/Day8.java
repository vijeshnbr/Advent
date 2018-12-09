package com.vn.advent.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Stream;

public class Day8 implements Solution {

	private static int[] arr;

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day8();
		solution.run();
	}

	@Override
	public void partOne(Stream<String> lines) {
		Node ROOT = initialize(lines);
		LOGGER.log(Level.INFO, "TREE : {0}", ROOT);
		System.out.print(sumOfMetadatas(ROOT));
	}

	@Override
	public void partTwo(Stream<String> lines) {
		Node ROOT = initialize(lines);
		LOGGER.log(Level.INFO, "TREE : {0}", ROOT);
		System.out.print(ROOT.getValue());
	}

	private Node initialize(Stream<String> lines) {
		String licenseFile = lines.findFirst()
			.get();
		arr = Stream.of(licenseFile.split(" "))
			.map(Integer::parseInt)
			.mapToInt(i -> (int) i)
			.toArray();
		Node ROOT = new Node(0);
		return ROOT;
	}

	int sumOfMetadatas(Node n) {
		int sumOfOwnMetadata = n.metadatas.stream()
			.mapToInt(Metadata::getValue)
			.sum();
		int sumOfMetadataOfChildren = n.children.stream()
			.mapToInt(child -> sumOfMetadatas(child))
			.sum();
		return sumOfOwnMetadata + sumOfMetadataOfChildren;
	}

	public class Node {
		final int id;
		final Header header;
		final List<Node> children = new ArrayList<>();
		final List<Metadata> metadatas = new ArrayList<>();

		Node(int index) {
			this.id = index;
			int noOfChildren = arr[index];
			int noOfMetadatas = arr[index + 1];
			Header h = new Header(noOfChildren, noOfMetadatas);
			this.header = h;
			populateChildren();
			populateMetadatas();
		}

		int length() {
			if (children.size() == 0) {
				return 2 + header.noOfMetadata;
			}
			int lengthOfAllChildren = children.stream()
				.mapToInt(Node::length)
				.sum();
			return 2 + lengthOfAllChildren + header.noOfMetadata;
		}

		int getValue() {
			int value = 0;
			if (header.noOfChildren == 0) {
				value = metadatas.stream()
					.mapToInt(Metadata::getValue)
					.sum();
			} else {
				value = metadatas.stream()
					.mapToInt(Metadata::getValue)
					.filter(metadata -> metadata > 0 && metadata <= header.noOfChildren)
					.map(i -> i - 1)
					.mapToObj(children::get)
					.mapToInt(Node::getValue)
					.sum();
			}
			return value;
		}

		void populateChildren() {
			Optional<Integer> indexOfNextChild = getIndexOfNextChild();
			while (indexOfNextChild.isPresent()) {
				children.add(new Node(indexOfNextChild.get()));
				indexOfNextChild = getIndexOfNextChild();
			}
		}

		Optional<Integer> getIndexOfNextChild() {
			Optional<Integer> childNodeIndex = Optional.empty();
			int noOfChildren = header.noOfChildren;
			int noOfChildrenAlreadyAdded = children.size();
			if (noOfChildren > 0) {
				if (noOfChildrenAlreadyAdded == 0) {
					childNodeIndex = Optional.of(id + 2);
				} else if (noOfChildrenAlreadyAdded < noOfChildren) {
					childNodeIndex = Optional.of(id + 2 + children.stream()
						.mapToInt(Node::length)
						.sum());
				}
			}
			return childNodeIndex;
		}

		Optional<Integer> getIndexOfFirstMetadata() {
			Optional<Integer> metaDataIndex = Optional.empty();
			int noOfMetadata = header.noOfMetadata;
			if (noOfMetadata > 0) {
				if (id == 0) {
					// ROOT Node
					metaDataIndex = Optional.of(arr.length - noOfMetadata);
				} else if (header.noOfChildren == 0) {
					// Node has NO children
					metaDataIndex = Optional.of(id + 2);
				} else {
					// Node has 1 or more children
					metaDataIndex = Optional.of(id + 2 + children.stream()
						.mapToInt(Node::length)
						.sum());
				}
			}
			return metaDataIndex;
		}

		void populateMetadatas() {
			getIndexOfFirstMetadata().ifPresent(index -> {
				for (int i = index; i < index + header.noOfMetadata; i++) {
					Metadata m = new Metadata(i);
					metadatas.add(m);
				}
			});
		}

		@Override
		public String toString() {
			return "Node [id=" + id + ", header=" + header + ",\nchildren=" + children + ",\n metadatas=" + metadatas
					+ "]";
		}
	}

	private class Header {
		final int noOfChildren;
		final int noOfMetadata;

		public Header(int noOfChildren, int noOfMetadata) {
			this.noOfChildren = noOfChildren;
			this.noOfMetadata = noOfMetadata;
		}

		@Override
		public String toString() {
			return "[noOfChildren=" + noOfChildren + ", noOfMetadata=" + noOfMetadata + "]";
		}
	}

	public class Metadata {
		final int id;
		final int value;

		Metadata(int index) {
			this.id = index;
			this.value = arr[index];
		}

		public int getValue() {
			return value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}

	@Override
	public String getInputFileName() {
		return "input_8";
	}

}
