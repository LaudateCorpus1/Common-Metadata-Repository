(ns cmr.umm-spec.test.validation.related-url
  "This has tests for UMM Related URL validations."
  (:require
   [clojure.test :refer :all]
   [cmr.common.services.errors :as e]
   [cmr.umm-spec.dif-util :as dif-util]
   [cmr.umm-spec.models.umm-collection-models :as coll]
   [cmr.umm-spec.models.umm-common-models :as c]
   [cmr.umm-spec.related-url :as url]
   [cmr.umm-spec.test.validation.umm-spec-validation-test-helpers :as h]
   [cmr.umm-spec.util :as su]
   [cmr.umm-spec.validation.umm-spec-validation-core :as v]
   [cmr.umm-spec.xml-to-umm-mappings.echo10.related-url :as echo10-url]))

(deftest collection-related-urls-validation
  (testing "Valid related urls"
    (h/assert-warnings-valid
     (coll/map->UMM-C
                     {:RelatedUrls [{:URL "http://fresc.usgs.gov/products/dataset/moorhen_telemetry.zip"
                                     :URLContentType "DistributionURL"
                                     :Description "Description"
                                     :Type "GET DATA"
                                     :Subtype "Earthdata Search"}
                                    {:URL "http://gce-lter.marsci.uga.edu/lter/asp/db/send_eml.asp?detail=full&missing=NaN&delimiter=%09&accession=FNG-GCEM-0401"
                                     :URLContentType "PublicationURL"
                                     :Description "Description"
                                     :Type "VIEW RELATED INFORMATION"
                                     :Subtype "USER'S GUIDE"}
                                    {:URL "http://www.google.com"
                                     :URLContentType "VisualizationURL"
                                     :Description "Description"
                                     :Type "GET RELATED VISUALIZATION"
                                     :Subtype "MAP"}]})))

  (testing "Multiple invalid related urls"
    (h/assert-warnings-multiple-invalid
     (coll/map->UMM-C
      {:RelatedUrls [{:URL "http:\\\\fresc.usgs.gov\\products\\dataset\\moorhen_telemetry.zip"
                      :URLContentType "DistributionURL"
                      :Description "Description"
                      :Type "GET DATA"
                      :Subtype "Earthdata Search"}
                     {:URL "http://ingrid.ldgo.columbia.edu/SOURCES/.IGOSS/.fsu/|u|u+|u|v/dods"
                      :URLContentType "PublicationURL"
                      :Type "VIEW RELATED INFORMATION"
                      :Subtype "USER'S GUIDE"}
                     {:URL "https://www.foo.com"
                      :URLContentType "Bad URLContentType"
                      :Description "Description"
                      :Type "VIEW RELATED INFORMATION"
                      :Subtype "USER'S GUIDE"}
                     {:URL "https://www.bar.com"
                      :URLContentType "PublicationURL"
                      :Description "Description"
                      :Type "Bad Type"
                      :Subtype "USER'S GUIDE"}
                     {:URL "https://www.foobar.com"
                      :URLContentType "PublicationURL"
                      :Description "Description"
                      :Type "VIEW RELATED INFORMATION"
                      :Subtype "Bad Subtype"}
                     {:URL "https://www.baz.com"
                      :URLContentType "PublicationURL"
                      :Description "Description"
                      :Type "VIEW RELATED INFORMATION"
                      :Subtype "USER'S GUIDE"
                      :GetService {:MimeType "application/html"
                                   :FullName "Not provided"
                                   :DataID "Not provided"
                                   :Protocol "Not provided"}}
                     {:URL "https://www.baz.com"
                      :URLContentType "PublicationURL"
                      :Description "Description"
                      :Type "VIEW RELATED INFORMATION"
                      :Subtype "USER'S GUIDE"
                      :GetData {:Size 10.0
                                :Unit "MB"
                                :Format "Not provided"}}]})
     [{:path [:RelatedUrls 4]
       :errors ["URLContentType: PublicationURL, Type: VIEW RELATED INFORMATION, Subtype: Bad Subtype is not a vaild URLContentType/Type/Subtype combination."]}
      {:path [:RelatedUrls 3]
       :errors ["URLContentType: PublicationURL, Type: Bad Type, Subtype: USER'S GUIDE is not a vaild URLContentType/Type/Subtype combination."]}
      {:path [:RelatedUrls 2]
       :errors ["URLContentType: Bad URLContentType, Type: VIEW RELATED INFORMATION, Subtype: USER'S GUIDE is not a vaild URLContentType/Type/Subtype combination."]}
      {:path [:RelatedUrls 1 :URL]
       :errors ["[http://ingrid.ldgo.columbia.edu/SOURCES/.IGOSS/.fsu/|u|u+|u|v/dods] is not a valid URL"]}
      {:path [:RelatedUrls 0 :URL]
       :errors ["[http:\\\\fresc.usgs.gov\\products\\dataset\\moorhen_telemetry.zip] is not a valid URL"]}
      {:path [:RelatedUrls 1]
       :errors ["RelatedUrl does not have a description."]}
      {:path [:RelatedUrls 5]
       :errors ["Only URLContentType: DistributionURL Type: GET SERVICE can contain GetService, RelatedUrl contains URLContentType: PublicationURL Type: VIEW RELATED INFORMATION"]}
      {:path [:RelatedUrls 6]
       :errors ["Only URLContentType: DistributionURL Type: GET DATA can contain GetData, RelatedUrl contains URLContentType: PublicationURL Type: VIEW RELATED INFORMATION"]}])))
;
(deftest collection-data-center-related-urls-validation
  (testing "Valid related urls"
    (h/assert-warnings-valid
     (coll/map->UMM-C
                     {:DataCenters
                      [{:ContactInformation
                        {:RelatedUrls [{:URL "http://fresc.usgs.gov/products/dataset/moorhen_telemetry.zip"
                                        :URLContentType "DataCenterURL"
                                        :Description "Description"
                                        :Type "HOME PAGE"}
                                       {:URL "http://gce-lter.marsci.uga.edu/lter/asp/db/send_eml.asp?detail=full&missing=NaN&delimiter=%09&accession=FNG-GCEM-0401"
                                        :URLContentType "DataCenterURL"
                                        :Description "Description"
                                        :Type "HOME PAGE"}]}}]})))
  (testing "Multiple invalid related urls"
    (h/assert-warnings-multiple-invalid
     (coll/map->UMM-C
      {:DataCenters
       [{:ContactInformation
         {:RelatedUrls [{:URL "http:\\\\fresc.usgs.gov\\products\\dataset\\moorhen_telemetry.zip"
                         :URLContentType "DataCenterURL"
                         :Type "HOME PAGE"}
                        {:URL "http://ingrid.ldgo.columbia.edu/SOURCES/.IGOSS/.fsu/|u|u+|u|v/dods"
                         :URLContentType "DataCenterURL"
                         :Description "Description"
                         :Type "HOME PAGE"}
                        {:URL "https://www.foo.com"
                         :URLContentType "Bad URLContentType"
                         :Description "Description"
                         :Type "HOME PAGE"}
                        {:URL "https://www.bar.com"
                         :URLContentType "DataCenterURL"
                         :Description "Description"
                         :Type "Bad Type"}
                        {:URL "https://www.foobar.com"
                         :URLContentType "DataCenterURL"
                         :Description "Description"
                         :Type "HOME PAGE"
                         :Subtype "Bad Subtype"}]}}]})
     [{:path [:DataCenters 0 :ContactInformation :RelatedUrls 0 :URL]
       :errors ["[http:\\\\fresc.usgs.gov\\products\\dataset\\moorhen_telemetry.zip] is not a valid URL"]}
      {:path [:DataCenters 0 :ContactInformation :RelatedUrls 0]
       :errors ["RelatedUrl does not have a description."]}
      {:path [:DataCenters 0 :ContactInformation :RelatedUrls 3]
       :errors ["URLContentType: DataCenterURL, Type: Bad Type, Subtype: null is not a vaild URLContentType/Type/Subtype combination."]}
      {:path [:DataCenters 0 :ContactInformation :RelatedUrls 4]
       :errors ["URLContentType: DataCenterURL, Type: HOME PAGE, Subtype: Bad Subtype is not a vaild URLContentType/Type/Subtype combination."]}
      {:path [:DataCenters 0 :ContactInformation :RelatedUrls 2]
       :errors ["URLContentType: Bad URLContentType, Type: HOME PAGE, Subtype: null is not a vaild URLContentType/Type/Subtype combination."
                "URLContentType must be DataCenterURL for DataCenter RelatedUrls"]}
      {:path [:DataCenters 0 :ContactInformation :RelatedUrls 1 :URL]
       :errors ["[http://ingrid.ldgo.columbia.edu/SOURCES/.IGOSS/.fsu/|u|u+|u|v/dods] is not a valid URL"]}]))
  (testing "Contact Persons valid related urls"
    (h/assert-warnings-valid
     (coll/map->UMM-C
                     {:DataCenters
                      [{:ContactPersons
                        [{:ContactInformation
                          {:RelatedUrls [{:URL "http://fresc.usgs.gov/products/dataset/moorhen_telemetry.zip"
                                          :URLContentType "DataContactURL"
                                          :Description "Description"
                                          :Type "HOME PAGE"}
                                         {:URL "http://gce-lter.marsci.uga.edu/lter/asp/db/send_eml.asp?detail=full&missing=NaN&delimiter=%09&accession=FNG-GCEM-0401"
                                          :URLContentType "DataContactURL"
                                          :Description "Description"
                                          :Type "HOME PAGE"}]}}]}]})))
  (testing "Contact Persons multiple invalid related urls"
    (h/assert-warnings-multiple-invalid
     (coll/map->UMM-C
      {:DataCenters
       [{:ContactPersons
         [{:ContactInformation
           {:RelatedUrls [{:URL "http:\\\\fresc.usgs.gov\\products\\dataset\\moorhen_telemetry.zip"
                           :URLContentType "DataContactURL"
                           :Description "Description"
                           :Type "HOME PAGE"}
                          {:URL "http://ingrid.ldgo.columbia.edu/SOURCES/.IGOSS/.fsu/|u|u+|u|v/dods"
                           :URLContentType "DataContactURL"
                           :Description "Description"
                           :Type "HOME PAGE"}
                          {:URL "https://www.foo.com"
                           :URLContentType "Bad URLContentType"
                           :Type "HOME PAGE"}
                          {:URL "https://www.bar.com"
                           :URLContentType "DataContactURL"
                           :Description "Description"
                           :Type "Bad Type"}
                          {:URL "https://www.foobar.com"
                           :URLContentType "DataContactURL"
                           :Description "Description"
                           :Type "HOME PAGE"
                           :Subtype "Bad Subtype"}]}}]}]})
     [{:path [:DataCenters 0 :ContactPersons 0 :ContactInformation :RelatedUrls 0 :URL]
       :errors ["[http:\\\\fresc.usgs.gov\\products\\dataset\\moorhen_telemetry.zip] is not a valid URL"]}
      {:path [:DataCenters 0 :ContactPersons 0 :ContactInformation :RelatedUrls 3]
       :errors ["URLContentType: DataContactURL, Type: Bad Type, Subtype: null is not a vaild URLContentType/Type/Subtype combination."]}
      {:path [:DataCenters 0 :ContactPersons 0 :ContactInformation :RelatedUrls 4]
       :errors ["URLContentType: DataContactURL, Type: HOME PAGE, Subtype: Bad Subtype is not a vaild URLContentType/Type/Subtype combination."]}
      {:path [:DataCenters 0 :ContactPersons 0 :ContactInformation :RelatedUrls 2]
       :errors ["RelatedUrl does not have a description."
                "URLContentType: Bad URLContentType, Type: HOME PAGE, Subtype: null is not a vaild URLContentType/Type/Subtype combination."
                "URLContentType must be DataContactURL for ContactPersons or ContactGroups RelatedUrls"]}
      {:path [:DataCenters 0 :ContactPersons 0 :ContactInformation :RelatedUrls 1 :URL]
       :errors ["[http://ingrid.ldgo.columbia.edu/SOURCES/.IGOSS/.fsu/|u|u+|u|v/dods] is not a valid URL"]}]))
  (testing "Contact Groups valid related urls"
    (h/assert-warnings-valid
     (coll/map->UMM-C
                     {:DataCenters
                      [{:ContactGroups
                        [{:ContactInformation
                          {:RelatedUrls [{:URL "http://fresc.usgs.gov/products/dataset/moorhen_telemetry.zip"
                                          :URLContentType "DataContactURL"
                                          :Description "Description"
                                          :Type "HOME PAGE"}
                                         {:URL "http://gce-lter.marsci.uga.edu/lter/asp/db/send_eml.asp?detail=full&missing=NaN&delimiter=%09&accession=FNG-GCEM-0401"
                                          :URLContentType "DataContactURL"
                                          :Description "Description"
                                          :Type "HOME PAGE"}]}}]}]})))
  (testing "Contact Groups multiple invalid related urls"
    (h/assert-warnings-multiple-invalid
     (coll/map->UMM-C
      {:DataCenters
       [{:ContactGroups
         [{:ContactInformation
           {:RelatedUrls [{:URL "http:\\\\fresc.usgs.gov\\products\\dataset\\moorhen_telemetry.zip"
                           :URLContentType "DataContactURL"
                           :Type "HOME PAGE"}
                          {:URL "http://ingrid.ldgo.columbia.edu/SOURCES/.IGOSS/.fsu/|u|u+|u|v/dods"
                           :URLContentType "DataContactURL"
                           :Description "Description"
                           :Type "HOME PAGE"}
                          {:URL "https://www.foo.com"
                           :URLContentType "Bad URLContentType"
                           :Description "Description"
                           :Type "HOME PAGE"}
                          {:URL "https://www.bar.com"
                           :URLContentType "DataContactURL"
                           :Description "Description"
                           :Type "Bad Type"}
                          {:URL "https://www.foobar.com"
                           :URLContentType "DataContactURL"
                           :Description "Description"
                           :Type "HOME PAGE"
                           :Subtype "Bad Subtype"}]}}]}]})
     [{:path [:DataCenters 0 :ContactGroups 0 :ContactInformation :RelatedUrls 0 :URL]
       :errors ["[http:\\\\fresc.usgs.gov\\products\\dataset\\moorhen_telemetry.zip] is not a valid URL"]}
      {:path [:DataCenters 0 :ContactGroups 0 :ContactInformation :RelatedUrls 0]
       :errors ["RelatedUrl does not have a description."]}
      {:path [:DataCenters 0 :ContactGroups 0 :ContactInformation :RelatedUrls 3]
       :errors ["URLContentType: DataContactURL, Type: Bad Type, Subtype: null is not a vaild URLContentType/Type/Subtype combination."]}
      {:path [:DataCenters 0 :ContactGroups 0 :ContactInformation :RelatedUrls 4]
       :errors ["URLContentType: DataContactURL, Type: HOME PAGE, Subtype: Bad Subtype is not a vaild URLContentType/Type/Subtype combination."]}
      {:path [:DataCenters 0 :ContactGroups 0 :ContactInformation :RelatedUrls 2]
       :errors ["URLContentType: Bad URLContentType, Type: HOME PAGE, Subtype: null is not a vaild URLContentType/Type/Subtype combination."
                "URLContentType must be DataContactURL for ContactPersons or ContactGroups RelatedUrls"]}
      {:path [:DataCenters 0 :ContactGroups 0 :ContactInformation :RelatedUrls 1 :URL]
       :errors ["[http://ingrid.ldgo.columbia.edu/SOURCES/.IGOSS/.fsu/|u|u+|u|v/dods] is not a valid URL"]}])))

(deftest collection-contact-persons-related-urls-validation
  (testing "Valid related urls"
    (h/assert-warnings-valid
     (coll/map->UMM-C
                     {:ContactPersons
                      [{:ContactInformation
                        {:RelatedUrls [{:URL "http://fresc.usgs.gov/products/dataset/moorhen_telemetry.zip"
                                        :URLContentType "DataContactURL"
                                        :Description "Description"
                                        :Type "HOME PAGE"}
                                       {:URL "http://gce-lter.marsci.uga.edu/lter/asp/db/send_eml.asp?detail=full&missing=NaN&delimiter=%09&accession=FNG-GCEM-0401"
                                        :URLContentType "DataContactURL"
                                        :Description "Description"
                                        :Type "HOME PAGE"}]}}]})))
  (testing "Multiple invalid related urls"
    (h/assert-warnings-multiple-invalid
     (coll/map->UMM-C
      {:ContactPersons
       [{:ContactInformation
         {:RelatedUrls [{:URL "http:\\\\fresc.usgs.gov\\products\\dataset\\moorhen_telemetry.zip"
                         :URLContentType "DataContactURL"
                         :Type "HOME PAGE"}
                        {:URL "http://ingrid.ldgo.columbia.edu/SOURCES/.IGOSS/.fsu/|u|u+|u|v/dods"
                         :URLContentType "DataContactURL"
                         :Description "Description"
                         :Type "HOME PAGE"}
                        {:URL "https://www.foo.com"
                         :URLContentType "Bad URLContentType"
                         :Description "Description"
                         :Type "HOME PAGE"}
                        {:URL "https://www.bar.com"
                         :URLContentType "DataContactURL"
                         :Description "Description"
                         :Type "Bad Type"}
                        {:URL "https://www.foobar.com"
                         :URLContentType "DataContactURL"
                         :Description "Description"
                         :Type "HOME PAGE"
                         :Subtype "Bad Subtype"}]}}]})
     [{:path [:ContactPersons 0 :ContactInformation :RelatedUrls 0 :URL]
       :errors ["[http:\\\\fresc.usgs.gov\\products\\dataset\\moorhen_telemetry.zip] is not a valid URL"]}
      {:path [:ContactPersons 0 :ContactInformation :RelatedUrls 0]
       :errors ["RelatedUrl does not have a description."]}
      {:path [:ContactPersons 0 :ContactInformation :RelatedUrls 3]
       :errors ["URLContentType: DataContactURL, Type: Bad Type, Subtype: null is not a vaild URLContentType/Type/Subtype combination."]}
      {:path [:ContactPersons 0 :ContactInformation :RelatedUrls 4]
       :errors ["URLContentType: DataContactURL, Type: HOME PAGE, Subtype: Bad Subtype is not a vaild URLContentType/Type/Subtype combination."]}
      {:path [:ContactPersons 0 :ContactInformation :RelatedUrls 2]
       :errors ["URLContentType: Bad URLContentType, Type: HOME PAGE, Subtype: null is not a vaild URLContentType/Type/Subtype combination."
                "URLContentType must be DataContactURL for ContactPersons or ContactGroups RelatedUrls"]}
      {:path [:ContactPersons 0 :ContactInformation :RelatedUrls 1 :URL]
       :errors ["[http://ingrid.ldgo.columbia.edu/SOURCES/.IGOSS/.fsu/|u|u+|u|v/dods] is not a valid URL"]}])))


(deftest collection-contact-groups-related-urls-validation
  (testing "Valid related urls"
    (h/assert-warnings-valid
     (coll/map->UMM-C
                     {:ContactGroups
                      [{:ContactInformation
                        {:RelatedUrls [{:URL "http://fresc.usgs.gov/products/dataset/moorhen_telemetry.zip"
                                        :URLContentType "DataContactURL"
                                        :Description "Description"
                                        :Type "HOME PAGE"}
                                       {:URL "http://gce-lter.marsci.uga.edu/lter/asp/db/send_eml.asp?detail=full&missing=NaN&delimiter=%09&accession=FNG-GCEM-0401"
                                        :URLContentType "DataContactURL"
                                        :Description "Description"
                                        :Type "HOME PAGE"}]}}]})))
  (testing "Multiple invalid related urls"
    (h/assert-warnings-multiple-invalid
     (coll/map->UMM-C
      {:ContactGroups
       [{:ContactInformation
         {:RelatedUrls [{:URL "http:\\\\fresc.usgs.gov\\products\\dataset\\moorhen_telemetry.zip"
                         :URLContentType "DataContactURL"
                         :Type "HOME PAGE"}
                        {:URL "http://ingrid.ldgo.columbia.edu/SOURCES/.IGOSS/.fsu/|u|u+|u|v/dods"
                         :URLContentType "DataContactURL"
                         :Description "Description"
                         :Type "HOME PAGE"}
                        {:URL "https://www.foo.com"
                         :URLContentType "Bad URLContentType"
                         :Description "Description"
                         :Type "HOME PAGE"}
                        {:URL "https://www.bar.com"
                         :URLContentType "DataContactURL"
                         :Description "Description"
                         :Type "Bad Type"}
                        {:URL "https://www.foobar.com"
                         :URLContentType "DataContactURL"
                         :Description "Description"
                         :Type "HOME PAGE"
                         :Subtype "Bad Subtype"}]}}]})
     [{:path [:ContactGroups 0 :ContactInformation :RelatedUrls 0 :URL]
       :errors ["[http:\\\\fresc.usgs.gov\\products\\dataset\\moorhen_telemetry.zip] is not a valid URL"]}
      {:path [:ContactGroups 0 :ContactInformation :RelatedUrls 0]
       :errors ["RelatedUrl does not have a description."]}
      {:path [:ContactGroups 0 :ContactInformation :RelatedUrls 3]
       :errors ["URLContentType: DataContactURL, Type: Bad Type, Subtype: null is not a vaild URLContentType/Type/Subtype combination."]}
      {:path [:ContactGroups 0 :ContactInformation :RelatedUrls 4]
       :errors ["URLContentType: DataContactURL, Type: HOME PAGE, Subtype: Bad Subtype is not a vaild URLContentType/Type/Subtype combination."]}
      {:path [:ContactGroups 0 :ContactInformation :RelatedUrls 2]
       :errors ["URLContentType: Bad URLContentType, Type: HOME PAGE, Subtype: null is not a vaild URLContentType/Type/Subtype combination."
                "URLContentType must be DataContactURL for ContactPersons or ContactGroups RelatedUrls"]}
      {:path [:ContactGroups 0 :ContactInformation :RelatedUrls 1 :URL]
       :errors ["[http://ingrid.ldgo.columbia.edu/SOURCES/.IGOSS/.fsu/|u|u+|u|v/dods] is not a valid URL"]}])))

(deftest collection-related-url-content-type-test
  (testing "All valid URLContentType/Type/Subtype combinations"
    (doseq [URLContentType (keys su/valid-url-content-types-map)
            :let [valid-types (su/valid-types-for-url-content-type URLContentType)]]
      (doseq [Type valid-types
              ;; Nil is a valid Subtype for any URLContentType/Type combination. So we add it to valid-sub-types
              :let [valid-sub-types (conj (su/valid-subtypes-for-type URLContentType Type) nil)]]
        (doseq [Subtype valid-sub-types]
          (h/assert-valid (coll/map->UMM-C
                           {:RelatedUrls [{:URL "http://fresc.usgs.gov/products/dataset/moorhen_telemetry.zip"
                                           :URLContentType URLContentType
                                           :Description "Description"
                                           :Type Type
                                           :Subtype Subtype}]})))))))

(deftest collection-collection-citations-related-urls-validation
  (testing "Valid related urls"
    (h/assert-warnings-valid
     (coll/map->UMM-C
                     {:CollectionCitations
                      [{:OnlineResource {:Linkage "http://fresc.usgs.gov/products/dataset/moorhen_telemetry.zip"}}
                       {:OnlineResource {:Linkage "http://gce-lter.marsci.uga.edu/lter/asp/db/send_eml.asp?detail=full&missing=NaN&delimiter=%09&accession=FNG-GCEM-0401"}}]})))
  (testing "Multiple invalid related urls"
    (h/assert-warnings-multiple-invalid
     (coll/map->UMM-C
      {:CollectionCitations
       [{:OnlineResource {:Linkage "http:\\\\fresc.usgs.gov\\products\\dataset\\moorhen_telemetry.zip"}}
        {:OnlineResource {:Linkage "http://ingrid.ldgo.columbia.edu/SOURCES/.IGOSS/.fsu/|u|u+|u|v/dods"}}]})
     [{:path [:CollectionCitations 0 :OnlineResource :Linkage]
       :errors ["[http:\\\\fresc.usgs.gov\\products\\dataset\\moorhen_telemetry.zip] is not a valid URL"]}
      {:path [:CollectionCitations 1 :OnlineResource :Linkage]
       :errors ["[http://ingrid.ldgo.columbia.edu/SOURCES/.IGOSS/.fsu/|u|u+|u|v/dods] is not a valid URL"]}])))

(deftest collection-publication-references-related-urls-validation
  (testing "Valid related urls"
    (h/assert-warnings-valid
     (coll/map->UMM-C
                     {:PublicationReferences
                      [{:OnlineResource {:Linkage "http://fresc.usgs.gov/products/dataset/moorhen_telemetry.zip"}}
                       {:OnlineResource {:Linkage "http://gce-lter.marsci.uga.edu/lter/asp/db/send_eml.asp?detail=full&missing=NaN&delimiter=%09&accession=FNG-GCEM-0401"}}]})))
  (testing "Multiple invalid related urls"
    (h/assert-warnings-multiple-invalid
     (coll/map->UMM-C
      {:PublicationReferences
       [{:OnlineResource {:Linkage "http:\\\\fresc.usgs.gov\\products\\dataset\\moorhen_telemetry.zip"}}
        {:OnlineResource {:Linkage "http://ingrid.ldgo.columbia.edu/SOURCES/.IGOSS/.fsu/|u|u+|u|v/dods"}}]})
     [{:path [:PublicationReferences 0 :OnlineResource :Linkage]
       :errors ["[http:\\\\fresc.usgs.gov\\products\\dataset\\moorhen_telemetry.zip] is not a valid URL"]}
      {:path [:PublicationReferences 1 :OnlineResource :Linkage]
       :errors ["[http://ingrid.ldgo.columbia.edu/SOURCES/.IGOSS/.fsu/|u|u+|u|v/dods] is not a valid URL"]}])))

(deftest dif-valid-url-types
 (testing "DIF hard-coded conversion table contains valid combinations"
  (doseq [url-type (vals dif-util/dif-url-content-type->umm-url-types)]
   (h/assert-warnings-valid
    (coll/map->UMM-C {:RelatedUrls [(merge
                                     url-type
                                     {:URL "https://www.foo.com"
                                      :Description "Description"})]})))))

(deftest echo10-valid-url-types
 (testing "ECHO10 hard-coded conversion table contains valid combinations"
  (doseq [url-type (vals echo10-url/online-resource-type->related-url-types)]
   (h/assert-warnings-valid
    (coll/map->UMM-C {:RelatedUrls [(merge
                                     url-type
                                     {:URL "https://www.foo.com"
                                      :Description "Description"})]})))))
