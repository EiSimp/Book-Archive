package group.project.bookarchive.utils;

public class PaginationUtils {
    public static class PaginationInfo {
        private int currentPage;
        private int totalPages;
        private int currentGroupStart;
        private int currentGroupEnd;
        private boolean hasMorePages;
        private boolean isNotFirstPage;
        private int totalItems;

        // Getters and setters for all fields
        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getCurrentGroupStart() {
            return currentGroupStart;
        }

        public void setCurrentGroupStart(int currentGroupStart) {
            this.currentGroupStart = currentGroupStart;
        }

        public int getCurrentGroupEnd() {
            return currentGroupEnd;
        }

        public void setCurrentGroupEnd(int currentGroupEnd) {
            this.currentGroupEnd = currentGroupEnd;
        }

        public boolean isHasMorePages() {
            return hasMorePages;
        }

        public void setHasMorePages(boolean hasMorePages) {
            this.hasMorePages = hasMorePages;
        }

        public boolean isNotFirstPage() {
            return isNotFirstPage;
        }

        public void setNotFirstPage(boolean isNotFirstPage) {
            this.isNotFirstPage = isNotFirstPage;
        }

        public int getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(int totalItems) {
            this.totalItems = totalItems;
        }
    }

    public static PaginationInfo calculatePagination(int currentPage, int totalItems, int itemsPerPage,
            int pageGroupSize) {
        PaginationInfo paginationInfo = new PaginationInfo();

        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        boolean hasMorePages = currentPage < totalPages;
        boolean isNotFirstPage = (currentPage != 1);

        int currentGroupStart = ((currentPage - 1) / pageGroupSize) * pageGroupSize + 1;
        int currentGroupEnd = Math.min(currentGroupStart + pageGroupSize - 1, totalPages);

        paginationInfo.setCurrentPage(currentPage);
        paginationInfo.setTotalPages(totalPages);
        paginationInfo.setCurrentGroupStart(currentGroupStart);
        paginationInfo.setCurrentGroupEnd(currentGroupEnd);
        paginationInfo.setHasMorePages(hasMorePages);
        paginationInfo.setNotFirstPage(isNotFirstPage);
        paginationInfo.setTotalItems(totalItems);

        return paginationInfo;
    }
}
