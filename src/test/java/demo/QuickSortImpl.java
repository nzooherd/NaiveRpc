package demo.api;

import java.util.Arrays;

public class QuickSortImpl implements QuickSort{
    @Override
    public int[] sort(int[] numbers) {
        Arrays.sort(numbers);
        return numbers;
    }
}
