package com.iuh.printshop.printshop_be.model;
import jakarta.persistence.*;

    @Entity
    @Table(name = "brands")
    public class Brand {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @Column(length = 80, nullable = false, unique = true)
        private String name;

        public Brand() {}

        public Brand(String name) {
            this.name = name;
        }

        // Getters & Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Brand{id=" + id + ", name='" + name + "'}";
        }
    }


