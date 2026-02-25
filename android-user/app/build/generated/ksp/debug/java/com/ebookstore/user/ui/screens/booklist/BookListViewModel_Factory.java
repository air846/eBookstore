package com.ebookstore.user.ui.screens.booklist;

import com.ebookstore.user.data.repository.BookRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class BookListViewModel_Factory implements Factory<BookListViewModel> {
  private final Provider<BookRepository> bookRepositoryProvider;

  public BookListViewModel_Factory(Provider<BookRepository> bookRepositoryProvider) {
    this.bookRepositoryProvider = bookRepositoryProvider;
  }

  @Override
  public BookListViewModel get() {
    return newInstance(bookRepositoryProvider.get());
  }

  public static BookListViewModel_Factory create(Provider<BookRepository> bookRepositoryProvider) {
    return new BookListViewModel_Factory(bookRepositoryProvider);
  }

  public static BookListViewModel newInstance(BookRepository bookRepository) {
    return new BookListViewModel(bookRepository);
  }
}
