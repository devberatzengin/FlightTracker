import 'package:flutter/material.dart';
import '../services/auth_service.dart';
import '../models/user_model.dart';

class AuthProvider extends ChangeNotifier {
  final AuthService _authService = AuthService();
  bool _isAuthenticated = false;
  bool _isLoading = false;
  String? _token;
  User? _currentUser;

  bool get isAuthenticated => _isAuthenticated;
  bool get isLoading => _isLoading;
  User? get currentUser => _currentUser;

  AuthProvider() {
    _checkAuth();
  }

  Future<void> _checkAuth() async {
    _token = await _authService.getToken();
    if (_token != null) {
      _currentUser = await _authService.getUserProfile();
      _isAuthenticated = _currentUser != null;
      if (!_isAuthenticated) {
        await _authService.logout();
        _token = null;
      }
    } else {
      _isAuthenticated = false;
    }
    notifyListeners();
  }

  Future<bool> login(String email, String password) async {
    _isLoading = true;
    notifyListeners();

    final success = await _authService.login(email, password);
    if (success) {
      _token = await _authService.getToken();
      _currentUser = await _authService.getUserProfile();
      _isAuthenticated = _currentUser != null;
    }

    _isLoading = false;
    notifyListeners();
    return success;
  }

  Future<bool> register(String firstName, String lastName, String email, String password, String phone) async {
    _isLoading = true;
    notifyListeners();

    final success = await _authService.register(firstName, lastName, email, password, phone);
    
    _isLoading = false;
    notifyListeners();
    return success;
  }

  Future<void> logout() async {
    await _authService.logout();
    _isAuthenticated = false;
    _token = null;
    _currentUser = null;
    notifyListeners();
  }

  Future<void> refreshUserProfile() async {
    _currentUser = await _authService.getUserProfile();
    notifyListeners();
  }
}
