/*
*
* COSC 051 Fall 2020
* Project #5 Code
* 
* Due on: 7 December 2020
*
* In accordance with class policies and Georgetown’s Honor Code,
* I certify that, with the exception of the class resources and those
* items noted below, I have neither given nor received any assistance* on this project.
*
* Note that you may use without citation any help from our TAs,
* professors, or any code taken from the course textbook.
*/


#include <iostream>
#include <iomanip>
#include <string>
#include <vector>
#include <fstream>
#include <math.h>
using namespace std;

class Delivery {
    friend istream& operator>>(istream&, Delivery&);
    friend ostream& operator<<(ostream&, Delivery&);
    public:
        Delivery();
        Delivery(string, string, string, unsigned int, unsigned int, unsigned int);
        void printAddress();
        void printItems();
        void setNextDelivery(Delivery*);
        Delivery* getNextDelivery() const;
        string getID() const;
        unsigned int getMilk() const;
        unsigned int getCheese() const;
        unsigned int getMeat() const;
    private:
        string orderID;
        string address1;
        string address2;
        unsigned int milk;
        unsigned int cheese;
        unsigned int meat;
        Delivery* nextDelivery;
};

Delivery::Delivery() {
    orderID = "";
    address1 = "";
    address2 = "";
    milk = 0;
    cheese = 0;
    meat = 0;
    nextDelivery = NULL;
}

Delivery::Delivery(string newID, string newA1, string newA2, unsigned int newMilk,unsigned int newCheese, unsigned int newMeat) {
    orderID = newID;
    address1 = newA1;
    address2 = newA2;
    milk = newMilk;
    cheese = newCheese;
    meat = newMeat;
    nextDelivery = NULL;
}

void Delivery::printAddress() {
    cout << setw(15) << "Order #:" << setw(15) << orderID << endl;
    cout << setw(15) << "Address line 1:" << setw(20) << address1 << endl;
    cout << setw(15) << "Address line 2:" << setw(20) << address2 << endl;
}

void Delivery::printItems() {
    cout << setw(15) << "Order #:" << setw(15) << orderID << endl;
    cout << setw(15) << "Milk cartons:" << setw(15) << milk << endl;
    cout << setw(15) << "Cheese blocks:" << setw(15) << cheese << endl;
    cout << setw(15) << "Meat packages:" << setw(15) << meat << endl;
}

void Delivery::setNextDelivery(Delivery* newNext) {
    nextDelivery = newNext;
}

Delivery* Delivery::getNextDelivery() const {
    return nextDelivery;
}

string Delivery::getID() const {
    return orderID;
}

unsigned int Delivery::getMilk() const {
    return milk;
}

unsigned int Delivery::getCheese() const {
    return cheese;
}

unsigned int Delivery::getMeat() const {
    return meat;
}


istream& operator>>(istream& in, Delivery& d) {
    string trash;

    in >> d.orderID;
    getline(in, trash);
    getline(in, d.address1);
    getline(in, d.address2);
    in >> d.milk >> d.cheese >> d.meat; 

    // cerr << "d.orderID: " << d.orderID << endl;
    // cerr << "d.address1: " << d.address1 << endl;
    // cerr << "d.address2: " << d.address2 << endl;
    // cerr << "d.milk: " << d.milk << endl;
    // cerr << "d.meat: " << d.meat << endl;
    // cerr << "d.cheese: " << d.cheese << endl;
    return in;
}

ostream& operator<<(ostream& out, Delivery& d) {
    // out << setw(15) << "Order #:" << setw(15) << d.orderID << endl;
    // out << setw(15) << "Address line 1:" << setw(20) << d.address1 << endl;
    // out << setw(15) << "Address line 2:" << setw(20) << d.address2 << endl;
    // out << d.milk << " " << d.cheese << " " << d.meat << endl; 

    out << d.orderID << endl << d.address1 << endl << d.address2 << endl;
    out << d.milk << " " << d.cheese << " " << d.meat << endl;




    return out; 
}

class Route {
    public:
        Route();
        Route(string ); // Create new Route and load file
        ~Route();
        int load_route_file(string); // Returns number of deliveries loaded
        void print_full_route();
        void print_addresses();
        void print_items();
        void count_items();
        void print_order(string);
        void clear_deliveries();
    private:
        void add_delivery(Delivery *); // Add parameter to list
        Delivery* find_delivery(string); // Return matching Delivery or nullptr
        Delivery* head; // Head pointer of linked list
};


Route::Route() { 
    head = NULL;
}

Route::Route(string filename) {
    load_route_file(filename); 
}


Route::~Route() {
    clear_deliveries(); 
}


int Route::load_route_file(string filename) {

    unsigned int count = 0;
    ifstream inputFile;
    inputFile.open(filename.c_str());

    head = NULL;
    Delivery * temp = new Delivery;

    if (!inputFile) {
        cout << "File not found, please enter a valid filename" << endl;
        return -1; 
    } else {
        // cerr << "In the else statment" << endl;
        while (inputFile >> *temp) {
            
            add_delivery(temp); 
            temp = new Delivery; 

            count++;
        }
        return count; 
    }
} 

void Route::print_full_route() {
    Delivery * temp = head;  
    while (temp != NULL){    
        cout << *temp;
        temp = temp->getNextDelivery();
    }  
    // cout << endl;
}


void Route::print_addresses() {
    Delivery * temp = head;  

    while (temp != NULL){    
        temp->printAddress(); 
        temp = temp->getNextDelivery();
    }  
    // cout << endl;
}

void Route::print_items() {
    Delivery * temp = head;  

    while (temp != NULL){    
        temp->printItems(); 
        temp = temp->getNextDelivery();
    }  
    // cout << endl;
}

void Route::count_items() {
    Delivery * temp = head; 
    unsigned int milkCount = 0;
    unsigned int cheeseCount = 0;
    unsigned int meatCount = 0;

    while (temp != NULL){    
        milkCount += temp->getMilk(); 
        cheeseCount += temp->getCheese();
        meatCount += temp->getMeat();

        temp = temp->getNextDelivery();
    }  
    cout << "----------" << endl;
    cout << "Totals for Route:" << endl;
    cout << setw(10) << "Milk" << setw(10) << milkCount << endl;
    cout << setw(10) << "Cheese" << setw(10) << cheeseCount << endl;
    cout << setw(10) << "Meat" << setw(10) << meatCount << endl;
    cout << "----------" << endl;

}
void Route::print_order(string ID) {
    if (find_delivery(ID)) {
        cout << "----------" << endl;
        cout << *find_delivery(ID);
        cout << "----------" << endl;
    } else {
        cout << "Invalid Order ID"; 
    }
    
}

void Route::add_delivery(Delivery * delivery) {
    // Delivery * temp = new Delivery();
    // *temp = *delivery; 
    // Link the new node to point at the head of the list  
    delivery->setNextDelivery(head);  
    // Make the head of the list point at the new node
    head = delivery; 

    // debugging statement
    // cerr << *delivery; 
}

Delivery* Route::find_delivery(string ID) {
    Delivery * temp = head;
    while (temp != NULL) {
        if (temp->getID() == ID) {
            return temp; 
        }
        temp = temp->getNextDelivery(); 
    }
    
    return NULL; 
}

void Route::clear_deliveries() {
    Delivery * temp = head;
    while (head != NULL) {
        head = head->getNextDelivery();
        delete(temp); 
        temp = head;
    } 
}

char getMenu();
void loadDeliveryFile(Route&, string); 


int main() {
    bool finished = false;
    char choice;
    string filename;
    bool deliveryData = false; 
    Route route;
    string orderNum;


    while (!finished) {
        choice = getMenu();

        switch (choice) {
            case 'l':
            case 'L':
                cout << "Enter name of delivery file to load: ";
                getline (cin, filename);
                route.load_route_file(filename);

                // if (route.load_route_file(filename) != -1) {
                //     deliveryData = true;
                // }
                deliveryData = true;
                 
                break;
            case 'd':
            case 'D':
                if (!deliveryData) {
                    cout << "No delivery data file has been loaded." << endl;
                    break; 
                }
                route.print_full_route(); 
                // cerr << "d was called" << endl;
                // cout << endl;
                break;
            case 'a':
            case 'A':
                if (!deliveryData) {
                    cout << "No delivery data file has been loaded." << endl;
                    break; 
                }
                route.print_addresses();
                break; 
            case 'i':
            case 'I':
                if (!deliveryData) {
                    cout << "No delivery data file has been loaded." << endl;
                    break; 
                }
                route.print_items();
                break;
            case 'c':
            case 'C':
                if (!deliveryData) {
                    cout << "No delivery data file has been loaded." << endl;
                    break; 
                }
                route.count_items();
                break;
            case 'f':
            case 'F':
                if (!deliveryData) {
                    cout << "No delivery data file has been loaded." << endl;
                    break; 
                }
                cout << "Enter the order number to print: "; 
                getline(cin, orderNum);
                route.print_order(orderNum);
                break;
            case 'q':
            case 'Q':
                finished = true;
                break;
            case 'x':
            case 'X':
                route.clear_deliveries();
                deliveryData = false; 
                break;
            default:
                cout << "Unknown menu selection." << endl;
                break;
        }
    }
    return 0;
}

char getMenu() {
    char choice;

    cout << endl << endl;

    cout << setw(45) << "Load delivery file" << setw(10) << "L" << endl;
    cout << setw(45) << "Show all delivery information" << setw(10) << "D" << endl;    
    cout << setw(45) << "Show all delivery addresses" << setw(10) << "A" << endl;
    cout << setw(45) << "Show delivery items" << setw(10) << "I" << endl;
    cout << setw(45) << "Show counts of items on route" << setw(10) << "C" << endl;
    cout << setw(45) << "Find an order by ID" << setw(10) << "F" << endl;
    cout << setw(45) << "Clear all loaded data" << setw(10) << "X" << endl;
    cout << setw(45) << "Exit the program" << setw(10) << "Q" << endl;

    cout << endl;
    cout << "Please enter your choice: ";
    cin >> choice;
    cin.clear();
    string trash;
    getline (cin, trash);

    return choice;
    cout << endl << endl; 
}

// void loadDeliveryFile(Route&, string filename) {
//     Delivery delivery;

//     ifstream inputFile;
//     inputFile.open(filename.c_str());
//     if (!inputFile) {
//         cout << "File not found, please enter a valid filename" << endl;
//     } else {
//         while (inputFile >> delivery) {
//             Route::

//         }
//     }
// }
