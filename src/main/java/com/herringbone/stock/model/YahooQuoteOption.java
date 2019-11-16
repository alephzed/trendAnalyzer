package com.herringbone.stock.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * YahooQUoteOptions entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "yahooquoteoptions")
public class YahooQuoteOption implements java.io.Serializable {

    // Fields
    private String tag;
    private String description;

    // Constructors

    /** default constructor */
    public YahooQuoteOption( ) {}

    /** full constructor */
    public YahooQuoteOption(String tag, String description ) {
        this.tag = tag;
        this.description = description;
    }

    // Property accessors
    @Id
    @Column(name = "TAG")
    public String getTag( ) {
        return this.tag;
    }

    public void setTag( String tag ) {
        this.tag = tag;
    }

    @Column(name = "DESCRIPTION")
    public String getDescription( ) {
        return this.description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

}