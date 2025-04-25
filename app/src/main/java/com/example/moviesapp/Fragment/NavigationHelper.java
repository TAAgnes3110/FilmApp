package com.example.moviesapp.Fragment;

import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.moviesapp.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class NavigationHelper {
    /**
     * Chuyển đến fragment mới trong container
     *
     * @param fragment Fragment hiện tại
     * @param targetFragment Fragment đích cần chuyển đến
     * @param tag Tag cho fragment (sử dụng cho back stack)
     * @param containerId ID của container chứa fragment
     * @param addToBackStack Có thêm vào back stack hay không
     */
    public static void navigateTo(
            Fragment fragment,
            Fragment targetFragment,
            String tag,
            int containerId,
            boolean addToBackStack
    ) {
        if (fragment.getActivity() == null) return;

        AppCompatActivity activity = (AppCompatActivity) fragment.getActivity();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Thêm hiệu ứng chuyển đổi
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        // Thay thế fragment hiện tại bằng fragment mới
        transaction.replace(containerId, targetFragment, tag);

        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }

    /**
     * Chuyển đến fragment mới trong container
     *
     * @param activity Activity hiện tại
     * @param fragment Fragment đích cần chuyển đến
     * @param tag Tag cho fragment (sử dụng cho back stack)
     * @param containerId ID của container chứa fragment
     * @param addToBackStack Có thêm vào back stack hay không
     */
    public static void navigateTo(
            AppCompatActivity activity,
            Fragment fragment,
            String tag,
            int containerId,
            boolean addToBackStack
    ) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Thêm hiệu ứng chuyển đổi
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        // Thay thế fragment hiện tại bằng fragment mới
        transaction.replace(containerId, fragment, tag);

        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }

    /**
     * Chuyển đến fragment mới trong container mặc định từ fragment hiện tại
     *
     * @param fragment Fragment hiện tại
     * @param targetFragment Fragment đích cần chuyển đến
     * @param tag Tag cho fragment (sử dụng cho back stack)
     */
    public static void navigateTo(
            Fragment fragment,
            Fragment targetFragment,
            String tag
    ) {
        if (fragment.getActivity() != null && fragment.getView() != null) {
            int containerId = ((ViewGroup) fragment.getView().getParent()).getId();
            navigateTo(fragment, targetFragment, tag, containerId, true);
        }
    }

    /**
     * Chuyển đến fragment mới trong container mặc định
     *
     * @param activity Activity hiện tại
     * @param fragment Fragment đích cần chuyển đến
     * @param tag Tag cho fragment (sử dụng cho back stack)
     */
    public static void navigateTo(
            AppCompatActivity activity,
            Fragment fragment,
            String tag
    ) {
        navigateTo(activity, fragment, tag, R.id.fragment_container, true);
    }

    /**
     * Quay lại fragment trước đó từ fragment hiện tại
     *
     * @param fragment Fragment hiện tại
     * @return true nếu có fragment để quay lại, false nếu back stack rỗng
     */
    public static boolean navigateBack(Fragment fragment) {
        if (fragment.getActivity() == null) return false;

        AppCompatActivity activity = (AppCompatActivity) fragment.getActivity();
        return navigateBack(activity);
    }

    /**
     * Quay lại fragment trước đó
     *
     * @param activity Activity hiện tại
     * @return true nếu có fragment để quay lại, false nếu back stack rỗng
     */
    public static boolean navigateBack(AppCompatActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Xóa tất cả fragment trong back stack và quay về fragment ban đầu
     * từ fragment hiện tại
     *
     * @param fragment Fragment hiện tại
     */
    public static void navigateToRoot(Fragment fragment) {
        if (fragment.getActivity() == null) return;

        AppCompatActivity activity = (AppCompatActivity) fragment.getActivity();
        navigateToRoot(activity);
    }

    /**
     * Xóa tất cả fragment trong back stack và quay về fragment ban đầu
     *
     * @param activity Activity hiện tại
     */
    public static void navigateToRoot(AppCompatActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Thêm fragment mới vào container mà không xóa fragment hiện tại
     * từ fragment hiện tại
     *
     * @param fragment Fragment hiện tại
     * @param targetFragment Fragment cần thêm
     * @param tag Tag cho fragment
     * @param containerId ID của container
     */
    public static void addFragment(
            Fragment fragment,
            Fragment targetFragment,
            String tag,
            int containerId
    ) {
        if (fragment.getActivity() == null) return;

        AppCompatActivity activity = (AppCompatActivity) fragment.getActivity();
        addFragment(activity, targetFragment, tag, containerId);
    }

    /**
     * Thêm fragment mới vào container mà không xóa fragment hiện tại
     *
     * @param activity Activity hiện tại
     * @param fragment Fragment cần thêm
     * @param tag Tag cho fragment
     * @param containerId ID của container
     */
    public static void addFragment(
            AppCompatActivity activity,
            Fragment fragment,
            String tag,
            int containerId
    ) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(containerId, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    /**
     * Kiểm tra fragment với tag đã tồn tại hay chưa
     * từ fragment hiện tại
     *
     * @param fragment Fragment hiện tại
     * @param tag Tag của fragment cần kiểm tra
     * @return true nếu fragment đã tồn tại, false nếu chưa
     */
    public static boolean isFragmentExist(Fragment fragment, String tag) {
        if (fragment.getActivity() == null) return false;

        AppCompatActivity activity = (AppCompatActivity) fragment.getActivity();
        return isFragmentExist(activity, tag);
    }

    /**
     * Kiểm tra fragment với tag đã tồn tại hay chưa
     *
     * @param activity Activity hiện tại
     * @param tag Tag của fragment cần kiểm tra
     * @return true nếu fragment đã tồn tại, false nếu chưa
     */
    public static boolean isFragmentExist(AppCompatActivity activity, String tag) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        return fragment != null;
    }

    /**
     * Quay lại fragment cụ thể dựa trên tag từ fragment hiện tại
     *
     * @param fragment Fragment hiện tại
     * @param tag Tag của fragment cần quay lại
     * @return true nếu tìm thấy và quay lại được, false nếu không
     */
    public static boolean navigateBackToTag(Fragment fragment, String tag) {
        if (fragment.getActivity() == null) return false;

        AppCompatActivity activity = (AppCompatActivity) fragment.getActivity();
        return navigateBackToTag(activity, tag);
    }

    /**
     * Quay lại fragment cụ thể dựa trên tag
     *
     * @param activity Activity hiện tại
     * @param tag Tag của fragment cần quay lại
     * @return true nếu tìm thấy và quay lại được, false nếu không
     */
    public static boolean navigateBackToTag(AppCompatActivity activity, String tag) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        boolean result = fragmentManager.popBackStackImmediate(tag, 0);
        return result;
    }

    /**
     * Thêm hiệu ứng tùy chỉnh khi chuyển đổi fragment từ fragment hiện tại
     *
     * @param fragment Fragment hiện tại
     * @param targetFragment Fragment đích
     * @param tag Tag cho fragment
     * @param containerId ID của container
     * @param enterAnim Animation khi vào
     * @param exitAnim Animation khi ra
     * @param popEnterAnim Animation khi quay lại vào
     * @param popExitAnim Animation khi quay lại ra
     */
    public static void navigateWithAnimation(
            Fragment fragment,
            Fragment targetFragment,
            String tag,
            int containerId,
            int enterAnim,
            int exitAnim,
            int popEnterAnim,
            int popExitAnim
    ) {
        if (fragment.getActivity() == null) return;

        AppCompatActivity activity = (AppCompatActivity) fragment.getActivity();
        navigateWithAnimation(activity, targetFragment, tag, containerId, enterAnim, exitAnim, popEnterAnim, popExitAnim);
    }

    /**
     * Thêm hiệu ứng tùy chỉnh khi chuyển đổi fragment
     *
     * @param activity Activity hiện tại
     * @param fragment Fragment đích
     * @param tag Tag cho fragment
     * @param containerId ID của container
     * @param enterAnim Animation khi vào
     * @param exitAnim Animation khi ra
     * @param popEnterAnim Animation khi quay lại vào
     * @param popExitAnim Animation khi quay lại ra
     */
    public static void navigateWithAnimation(
            AppCompatActivity activity,
            Fragment fragment,
            String tag,
            int containerId,
            int enterAnim,
            int exitAnim,
            int popEnterAnim,
            int popExitAnim
    ) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);
        transaction.replace(containerId, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }
}
