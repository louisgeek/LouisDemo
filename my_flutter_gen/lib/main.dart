import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:my_flutter_gen/core/network/api_client.dart';
import 'package:my_flutter_gen/feature/auth/data/local/app_database.dart';
import 'package:my_flutter_gen/feature/auth/data/local/dao/user_dao.dart';
import 'package:my_flutter_gen/feature/auth/data/local/local_data_source.dart';
import 'package:my_flutter_gen/feature/auth/data/remote/auth_api.dart';
import 'package:my_flutter_gen/feature/auth/data/remote/remote_data_source.dart';
import 'package:my_flutter_gen/feature/auth/data/repository/auth_repository_impl.dart';
import 'package:my_flutter_gen/feature/auth/domain/repository/auth_repository.dart';
import 'package:my_flutter_gen/feature/auth/domain/usecase/login_usecase.dart';
import 'package:my_flutter_gen/feature/auth/ui/auth_view_model.dart';
import 'package:my_flutter_gen/feature/auth/ui/login/login_page.dart';
import 'package:my_flutter_gen/feature/auth/ui/login/login_view_model.dart';
import 'package:my_flutter_gen/feature/auth/ui/register/register_page.dart';
import 'package:my_flutter_gen/feature/auth/ui/register/register_view_model.dart';
void main() {
  WidgetsFlutterBinding.ensureInitialized();
  // -- Init Network --
  ApiClient.instance.init(baseUrl: 'https://your-api-base-url.com');
  // -- Init Database & DataSources --
  final db = AppDatabase.instance;
  final userDao = UserDao(db);
  final localDataSource = LocalDataSource(userDao);
  final authApi = AuthApi(ApiClient.instance.dio);
  final remoteDataSource = RemoteDataSource(authApi);
  // -- Init Repository --
  final AuthRepository authRepository = AuthRepositoryImpl(
    remoteDataSource: remoteDataSource,
    localDataSource: localDataSource,
  );
  // -- Init UseCase --
  final loginUseCase = LoginUseCase(authRepository);
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(
          create: (_) => AuthViewModel(repository: authRepository)..init(),
        ),
        ChangeNotifierProvider(
          create: (_) => LoginViewModel(loginUseCase: loginUseCase),
        ),
        ChangeNotifierProvider(
          create: (_) => RegisterViewModel(repository: authRepository),
        ),
      ],
      child: const MyApp(),
    ),
  );
}
class MyApp extends StatelessWidget {
  const MyApp({super.key});
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'My Flutter Gen',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: Consumer<AuthViewModel>(
        builder: (context, authVM, _) {
          if (authVM.isLoading) {
            return const Scaffold(
              body: Center(child: CircularProgressIndicator()),
            );
          }
          return authVM.isLoggedIn ? const HomePage() : const LoginPage();
        },
      ),
      routes: {
        '/login': (_) => const LoginPage(),
        '/register': (_) => const RegisterPage(),
        '/home': (_) => const HomePage(),
      },
    );
  }
}
class HomePage extends StatelessWidget {
  const HomePage({super.key});
  @override
  Widget build(BuildContext context) {
    final authVM = context.watch<AuthViewModel>();
    return Scaffold(
      appBar: AppBar(
        title: const Text('Home'),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            tooltip: 'Logout',
            onPressed: () async {
              await authVM.logout();
              if (context.mounted) {
                Navigator.of(context).pushReplacementNamed('/login');
              }
            },
          ),
        ],
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.check_circle_outline, size: 80, color: Colors.green),
            const SizedBox(height: 16),
            Text(
              'Welcome, ${authVM.currentUser?.username ?? 'User'}!',
              style: Theme.of(context).textTheme.headlineMedium,
            ),
            const SizedBox(height: 8),
            Text(
              authVM.currentUser?.email ?? '',
              style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                    color: Colors.grey,
                  ),
            ),
          ],
        ),
      ),
    );
  }
}
