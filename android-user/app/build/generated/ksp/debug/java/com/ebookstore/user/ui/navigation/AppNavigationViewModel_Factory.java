package com.ebookstore.user.ui.navigation;

import com.ebookstore.user.data.repository.AuthRepository;
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
public final class AppNavigationViewModel_Factory implements Factory<AppNavigationViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  public AppNavigationViewModel_Factory(Provider<AuthRepository> authRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public AppNavigationViewModel get() {
    return newInstance(authRepositoryProvider.get());
  }

  public static AppNavigationViewModel_Factory create(
      Provider<AuthRepository> authRepositoryProvider) {
    return new AppNavigationViewModel_Factory(authRepositoryProvider);
  }

  public static AppNavigationViewModel newInstance(AuthRepository authRepository) {
    return new AppNavigationViewModel(authRepository);
  }
}
