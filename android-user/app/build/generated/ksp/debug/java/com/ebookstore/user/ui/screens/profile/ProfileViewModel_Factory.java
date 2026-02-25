package com.ebookstore.user.ui.screens.profile;

import com.ebookstore.user.data.repository.AuthRepository;
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
public final class ProfileViewModel_Factory implements Factory<ProfileViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<BookRepository> bookRepositoryProvider;

  public ProfileViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<BookRepository> bookRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.bookRepositoryProvider = bookRepositoryProvider;
  }

  @Override
  public ProfileViewModel get() {
    return newInstance(authRepositoryProvider.get(), bookRepositoryProvider.get());
  }

  public static ProfileViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<BookRepository> bookRepositoryProvider) {
    return new ProfileViewModel_Factory(authRepositoryProvider, bookRepositoryProvider);
  }

  public static ProfileViewModel newInstance(AuthRepository authRepository,
      BookRepository bookRepository) {
    return new ProfileViewModel(authRepository, bookRepository);
  }
}
