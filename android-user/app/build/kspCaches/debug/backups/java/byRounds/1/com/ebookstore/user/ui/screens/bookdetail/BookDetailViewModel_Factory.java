package com.ebookstore.user.ui.screens.bookdetail;

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
public final class BookDetailViewModel_Factory implements Factory<BookDetailViewModel> {
  private final Provider<BookRepository> bookRepositoryProvider;

  public BookDetailViewModel_Factory(Provider<BookRepository> bookRepositoryProvider) {
    this.bookRepositoryProvider = bookRepositoryProvider;
  }

  @Override
  public BookDetailViewModel get() {
    return newInstance(bookRepositoryProvider.get());
  }

  public static BookDetailViewModel_Factory create(
      Provider<BookRepository> bookRepositoryProvider) {
    return new BookDetailViewModel_Factory(bookRepositoryProvider);
  }

  public static BookDetailViewModel newInstance(BookRepository bookRepository) {
    return new BookDetailViewModel(bookRepository);
  }
}
