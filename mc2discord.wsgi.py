#!/usr/bin/python3.12
import sys
import os

from dotenv import load_dotenv

# Load environment variables from .env file
dotenv_path = os.path.join(os.path.dirname(__file__), '.env')
load_dotenv(dotenv_path)

from main import app as application

sys.path.insert(0, os.path.abspath(os.path.dirname(__file__)))