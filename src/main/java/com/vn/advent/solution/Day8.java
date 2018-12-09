package com.vn.advent.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Day8 implements Solution {

	private static int[] arr;

	public static void main(String[] args) {
		Solution solution = new Day8();
		solution.run();
	}

	public void partOne(Stream<String> lines) {
		String header = lines.findFirst().get();
		arr = Stream.of(header.split(" ")).map(Integer::parseInt)
				.mapToInt(i -> (int) i).toArray();

		Node ROOT = new Node(0);

		System.out.println(ROOT);

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
			// populateMetadatas();
			populateChildren();
		}

		int length() {
			if (children.size() == 0) {
				return 2 + header.noOfMetadata;
			}
			int lengthOfAllChildren = children.stream().mapToInt(Node::length)
					.sum();
			return 2 + lengthOfAllChildren + header.noOfMetadata;
		}

		void populateChildren() {
			while (getIndexOfNextChild().isPresent()) {
				children.add(new Node(getIndexOfNextChild().get()));
			}
		}
		Optional<Integer> getIndexOfNextChild() {
			Optional<Integer> childNodeIndex = Optional.empty();
			int noOfChildren = header.noOfChildren;
			int noOfChildrenAlreadyAdded = children.size();
			if (noOfChildren > 0) {
				if (noOfChildrenAlreadyAdded == 0) {
					childNodeIndex = Optional.of(id + 2);
				} else {
					childNodeIndex = Optional.of(
							2 + children.stream().mapToInt(Node::length).sum());
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
			return "Node [id=" + id + ", header=" + header + ", children="
					+ children + ", metadatas=" + metadatas + "]";
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
			return "Header [noOfChildren=" + noOfChildren + ", noOfMetadata="
					+ noOfMetadata + "]";
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
			return "Metadata [id=" + id + ", value=" + value + "]";
		}
	}

	public void partTwo(Stream<String> lines) {

	}

	@Override
	public String getInputFileName() {
		return "test_input";
	}

}
