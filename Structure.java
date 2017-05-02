package loops;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Structure
{
	private ArrayList<int[]> numStruct = new ArrayList<>();
	private ArrayList<Loop> loops = new ArrayList<>();
	private String structure;
	private boolean built = false;



	// private boolean silence = true;

	public Structure(String structure)
	{
		this.structure = structure;
		enumerator();
		listBuilder();
		listOwnership();
		bulgeRemover();
	}



	private void enumerator()
	{
		// long time = System.currentTimeMillis();

		int[] nucleotide; // 0 type, 1 target, 2 visited
		char symbol;

		for(int index = 0; index < this.structure.length(); index++)
		{
			symbol = this.structure.charAt(index);
			nucleotide = new int[3];

			switch(symbol)
			{
				case '.':
				{
					nucleotide[0] = 0;
					nucleotide[1] = -1;
					nucleotide[2] = 1;
					this.numStruct.add(nucleotide);
					break;
				}
				case '(':
				{
					nucleotide[0] = 1;
					nucleotide[1] = -1;
					nucleotide[2] = 0;
					this.numStruct.add(nucleotide);
					break;
				}
				case ')':
				{
					nucleotide[0] = -1;
					for(int index2 = index - 1; index2 >= 0; index2--)
					{
						int[] nucleotide2 = this.numStruct.get(index2);
						if(nucleotide2[0] == 1 && nucleotide2[2] == 0)
						{
							nucleotide[1] = index2;
							nucleotide[2] = 1;
							this.numStruct.add(nucleotide);
							nucleotide2[1] = index;
							nucleotide2[2] = 1;
							// this.numStruct.set(index2, nucleotide2);
							break;
						}
					}
					break;
				}
			}
		}
		// System.out.println(this.numStruct);

		// time = System.currentTimeMillis() - time;
		// System.out.println("Secondary structure enumerated in " + time +
		// " milliseconds.");
	}



	private void listBuilder()
	{
		// long time = System.currentTimeMillis();

		int loopName = 0;
		int[] symbol;
		boolean continuous = false;

		for(int index = 0; index < this.numStruct.size(); index++)
		{
			symbol = this.numStruct.get(index);
			if(symbol[0] == 1)
			{
				if(continuous)
					continue;
				else
				{
					Loop loop = new Loop(loopName, index, symbol[1], this.numStruct);
					this.loops.add(loop);
					continuous = true;
					loopName++;
				}
			}
			else
				continuous = false;
		}

		continuous = false;

		for(int index = this.numStruct.size() - 1; index >= 0; index--)
		{
			symbol = this.numStruct.get(index);
			if(symbol[0] == -1)
			{
				if(continuous)
					continue;
				else
				{
					Loop loop = new Loop(loopName, symbol[1], index, this.numStruct);
					this.loops.add(loop);
					continuous = true;
					loopName++;
				}
			}
			else
				continuous = false;
		}

		// listOwnership();

		// time = System.currentTimeMillis() - time;
		// System.out.println("Loops found in " + time + " milliseconds.");
	}



	private void listOwnership()
	{
		// long time = System.currentTimeMillis();

		Collections.sort(this.loops, new LoopComparator());

		Loop mainLoop;
		Loop testLoop;

		for(int index = 0; index < this.loops.size(); index++)
		{
			mainLoop = this.loops.get(index);
			for(int index2 = index + 1; index2 < this.loops.size(); index2++)
			{
				testLoop = this.loops.get(index2);
				if(testLoop.getEnd() == mainLoop.getEnd())
				{
					this.loops.remove(index2);
					index2--;
					continue;
				}

				if(testLoop.getEnd() < mainLoop.getEnd())
				{
					testLoop.addOwner(mainLoop);
				}
			}
		}

		boolean branch = true;
		int level = 0;
		LoopComparator comparator = new LoopComparator();

		for(int index = 0; index < this.loops.size(); index++)
		{
			mainLoop = this.loops.get(index);
			Collections.sort(mainLoop.getOwned(), comparator);
			Collections.sort(mainLoop.getOwners(), comparator);
			if(mainLoop.getOwners().size() == 0) mainLoop.setLevel(level);
			// else mainLoop.setLevel(Integer.MAX_VALUE);
		}

		while(branch)
		{
			branch = false;
			level++;

			for(int index = 0; index < this.loops.size(); index++)
			{
				mainLoop = this.loops.get(index);

				if(mainLoop.getLevel() < level) continue;

				testLoop = mainLoop.getOwners().get(0);

				if(mainLoop.getOwners().size() == 1)
				{
					mainLoop.setLevel(level);
					testLoop.addOwned(mainLoop);
				}
				else
					mainLoop.removeOwner(testLoop);

				branch = true;
			}
		}

		// time = System.currentTimeMillis() - time;
		// System.out.println("Loop ownership decided in " + time +
		// " milliseconds.");
	}



	private void bulgeRemover()
	{
		Loop mainLoop;
		Loop testLoop;

		for(int index = 0; index < this.loops.size(); index++)
		{
			mainLoop = this.loops.get(index);

			if(mainLoop.getName() == -1) continue;

			while(mainLoop.getOwned().size() == 1)
			{
				testLoop = mainLoop.getOwned().get(0);
				mainLoop.setOwned(testLoop.getOwned());
				testLoop.setName(-1);
				testLoop.getOwners().set(0, mainLoop);
			}
		}

		for(int index = 0; index < loops.size(); index++)
		{
			mainLoop = this.loops.get(index);

			if(mainLoop.getName() == -1) continue;

			if(mainLoop.getOwners().size() > 0)
			{
				if(mainLoop.getOwners().get(0).getName() == -1)
				{
					testLoop = mainLoop.getOwners().get(0);
					mainLoop.setOwners(testLoop.getOwners());
				}
			}
		}

		for(int index = 0; index < this.loops.size(); index++)
		{
			mainLoop = this.loops.get(index);
			if(mainLoop.getName() == -1)
			{
				this.loops.remove(index);
				index--;
			}
		}

		this.built = true;
	}



	public void loopPrinter()
	{
		for(int index = 0; index < this.loops.size(); index++)
		{
			System.out.println(this.loops.get(index));
		}
	}



	public Loop[] getHairpins()
	{
		if(!this.built) return null;

		ArrayList<Loop> hairpins = new ArrayList<>();

		for(int index = 0; index < this.loops.size(); index++)
		{
			Loop loop = this.loops.get(index);
			if(loop.getOwned().size() == 0) hairpins.add(loop);
		}

		Loop[] array = new Loop[hairpins.size()];
		hairpins.toArray(array);

		return array;
	}



	/*
	 * public Loop[] getWholeLoops() { if(!this.built) return null;
	 * 
	 * Loop[] wholeLoops = getHairpins(); for(int index = 0; index <
	 * wholeLoops.length; index++) { Loop loop = wholeLoops[index];
	 * while(loop.getOwners().size() == 1) { Loop owner =
	 * loop.getOwners().get(0); if(owner.getOwned().size() == 1) loop = owner;
	 * else break; } wholeLoops[index] = loop; }
	 * 
	 * return wholeLoops; }
	 */

	public Loop[] getSplitLoops()
	{
		if(!this.built) return null;

		ArrayList<Loop> splitLoops = new ArrayList<>();
		boolean hasSplit;
		
		Loop[] hairpins = this.getHairpins();
		for(int index = 0; index < hairpins.length; index++)
		{
			hasSplit = false;
			
			Loop currentLoop = hairpins[index];
			if(currentLoop.getOwners().size() == 0) continue;
			Loop parrentLoop = currentLoop.getOwners().get(0);
			
			for(int index2 = 0; index2 < parrentLoop.getOwned().size(); index2 ++)
			{
				Loop childLoop = parrentLoop.getOwned().get(index2);
				if(childLoop == currentLoop) continue;
				if(childLoop.getOwned().size() > 0) hasSplit = true;
			}
			
			if(!hasSplit && !(splitLoops.contains(parrentLoop))) splitLoops.add(parrentLoop);
		}

		/*for(int index = 0; index < this.loops.size(); index++)
		{
			hasSplit = false;
			Loop loop = this.loops.get(index);

			if(loop.getOwned().size() != 0) continue;

			while(loop.getOwners().size() == 1)
			{
				Loop owner = loop.getOwners().get(0);
				if(owner.getOwned().size() == 1)
					loop = owner;
				else if(!hasSplit)
				{
					boolean complex = false;
					for(int index2 = 0; index2 < owner.getOwned().size(); index2++)
					{
						Loop descendant = owner.getOwned().get(index2);
						if(descendant.getOwned().size() > 1) complex = true;
						// if(descendant.getSize() >);
					}
					if(!complex)
					{
						loop = owner;
						hasSplit = true;
					}
					else break;
				}
				else break;
			}

			splitLoops.add(loop);
		}

		for(int index = 0; index < splitLoops.size(); index++)
		{
			Loop loop = splitLoops.get(index);
			for(int index2 = index + 1; index2 < splitLoops.size(); index2++)
			{
				Loop testLoop = splitLoops.get(index2);
				if(loop.getStart() == testLoop.getStart()) splitLoops.remove(index2);
			}
		}*/
		
		if(splitLoops.size() == 0) return null;

		Loop[] array = new Loop[splitLoops.size()];
		splitLoops.toArray(array);

		return array;
	}



	public Loop[] getBranches()
	{
		if(!this.built) return null;

		ArrayList<Loop> branches = new ArrayList<>();

		for(int index = 0; index < this.loops.size(); index++)
		{
			Loop loop = this.loops.get(index);
			if(loop.getOwned().size() > 1) branches.add(loop);
		}

		Loop[] array = new Loop[branches.size()];
		branches.toArray(array);

		return array;
	}



	public ArrayList<Loop> getLoops()
	{
		return loops;
	}



	public void setLoops(ArrayList<Loop> loops)
	{
		this.loops = loops;
	}



	public String getStructure()
	{
		return structure;
	}



	public void setStructure(String structure)
	{
		this.structure = structure;
	}
	
	
	
	public static Integer findSplitLoopSize(Loop loop)
	{
		if(loop.getOwned().size() == 0) return null;
		
		int largestLoopIndex = 0;
				
		for(int index = 1; index < loop.getOwned().size(); index++)
		{
			Loop largestLoop = loop.getOwned().get(largestLoopIndex);
			Loop currentLoop = loop.getOwned().get(index);
			if(currentLoop.getSize() > largestLoop.getSize()) largestLoopIndex = index;
		}
		
		Loop largestLoop = loop.getOwned().get(largestLoopIndex);
		int finalLoopSize = loop.getSize();
		
		for(int index = 0; index < loop.getOwned().size(); index++)
		{
			Loop currentLoop = loop.getOwned().get(index);
			if(currentLoop == largestLoop) continue;
			else finalLoopSize = finalLoopSize - currentLoop.getSize(); 
		}
		
		return finalLoopSize;
	}
	
	
	
	public static double findStemPercentage(Loop loop)
	{
		int[] intElements = loop.getElements();
		
		return Structure.findStemPercentage(intElements);
	}
	
	
	
	public static double findStemPercentage(int[] intElements)
	{
		double[] doubleElements = new double[2];
		doubleElements[0] = (double)intElements[0];
		doubleElements[1] = (double)intElements[1];
		double results = (doubleElements[1]*2)/(doubleElements[1]*2 + doubleElements[0]);
		
		return results;
	}
	
	
	
	public static Double findSplitStemPercentage(Loop loop)
	{
		if(loop.getOwned().size() == 0) return null;
		
		int largestLoopIndex = 0;
		
		for(int index = 1; index < loop.getOwned().size(); index++)
		{
			Loop largestLoop = loop.getOwned().get(largestLoopIndex);
			Loop currentLoop = loop.getOwned().get(index);
			if(currentLoop.getSize() > largestLoop.getSize()) largestLoopIndex = index;
		}
		
		Loop largestLoop = loop.getOwned().get(largestLoopIndex);
		int[] loopElements = loop.getElements();
		
		for(int index = 0; index < loop.getOwned().size(); index++)
		{
			Loop currentLoop = loop.getOwned().get(index);
			if(currentLoop == largestLoop) continue;
			int[] currentLoopElements = currentLoop.getElements();
			loopElements[0] = loopElements[0] - currentLoopElements[0];
			loopElements[1] = loopElements[1] - currentLoopElements[1];
		}
		
		Double stemPercentage = Structure.findStemPercentage(loopElements);
		return stemPercentage;
	}
	
	
	
	public static Loop findLargestStem(Structure structure)
	{
		Loop[] loops = structure.getHairpins();
		
		Loop loop = null;
		int maxSize = 0;
		for(int index = 0; index < loops.length; index++)
		{
			Loop tempLoop = loops[index];
			int currentSize = tempLoop.getSize();
			if(currentSize > maxSize)
			{
				maxSize = currentSize;
				loop = tempLoop;
			}
		}
		
		return loop;
	}
	
	
	
	public static Loop findLargestSplitStem(Structure structure)
	{
		Loop[] loops = structure.getSplitLoops();
		if(loops == null) return null;
		
		Loop loop = null;
		int maxSize = 0;
		
		for(int index = 0; index < loops.length; index++)
		{
			Loop tempLoop = loops[index];
			int currentSize = tempLoop.getSize();
			
			if(currentSize > maxSize)
			{
				maxSize = currentSize;
				loop = tempLoop;
			}
		}	
		return loop;
	}



	public static void main(String[] args)
	{
		String structure = "..(((.(((((..(..(((((((....)))))))..((((((((((((((..((((((.(((((....(((((((..(((((((.(((((((((((((((((..(((....)))))))))))))).)))))).)))))))(((.....))).....)))))))....))))).))))))..))))))))))))))....)..)))))..))).((..((((((((((..........................................................(((((...((((......))))...)))))..........))))))))))..))..";
		//String structure = "...........................................(((((((.((.(((....((((((........)))))).))).)).)))))))..........................................................................................................................................................................................................................................";
		System.out.println(structure.length());
		Structure test = new Structure(structure);
		// test.enumerator();
		// test.listBuilder();
		// test.listOwnership();
		test.loopPrinter();
		System.out.println();
		//Loop loop = test.getSplitLoops()[0];
		//System.out.println(loop.getName());
		//System.out.println(Structure.findSplitLoopSize(loop));
		//System.out.println(Structure.findSplitStemPercentage(loop));
	}



	class LoopComparator implements Comparator<Loop>
	{

		@Override
		public int compare(Loop loop1, Loop loop2)
		{
			return loop1.getStart() - loop2.getStart();
		}

	}
}
