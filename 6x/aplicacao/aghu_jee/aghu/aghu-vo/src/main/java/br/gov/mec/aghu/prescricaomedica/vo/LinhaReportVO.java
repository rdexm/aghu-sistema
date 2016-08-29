package br.gov.mec.aghu.prescricaomedica.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;


public class LinhaReportVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8204576537155477043L;
	private String texto1;
	private String texto2;
	private String texto3;
	private String texto4;
	private String texto5;
	private String texto6;
	private String texto7;
	private String texto8;
	private String texto9;
	private Date data;
	private Date data1;
	private BigDecimal numero1;
	private BigDecimal numero2;
	private BigDecimal numero3;
	private Short numero4;
	private Short numero5;
	private Short numero12;
	private Long numero6;
	private Integer numero7;
	private Integer numero8;
	private String numero9;
	private DominioIndContaminacao dominio1;
	private DominioSituacao dominio2;
	private byte[] imagem;
	private Object object;
	private Integer numero10;
	private Integer numero11;
	
	private String siglaEsp;
	private Short seqEsp;
	
	public Integer getNumero10() {
		return numero10;
	}

	public void setNumero10(Integer numero10) {
		this.numero10 = numero10;
	}

	private List<LinhaReportVO> subReport1;
	private List<LinhaReportVO> subReport2;
	private List<LinhaReportVO> subReport3;
	private List<LinhaReportVO> subReport4;
	private List<LinhaReportVO> subReport5;
	private List<LinhaReportVO> subReport6;
	private List<LinhaReportVO> subReport7;
	private List<LinhaReportVO> subReport8;
	
	private Boolean bool;
	
	public LinhaReportVO(Date data, String texto1) {
		super();
		this.texto1 = texto1;
		this.data = data;
	}
	
	public LinhaReportVO(Date data, String texto1, String texto2) {
		super();
		this.texto1 = texto1;
		this.texto2 = texto2;
		this.data = data;
	}
	
	public LinhaReportVO(Date data, String texto1, Integer texto2) {
		super();
		this.texto1 = texto1;
		this.texto2 = String.valueOf(texto2);
		this.data = data;
	}
	
	public LinhaReportVO(String texto1, String texto2, String texto3, Boolean bool) {
		super();
		this.texto1 = texto1;
		this.texto2 = texto2;
		this.texto3 = texto3;
		this.bool = bool;
	}
	
	public LinhaReportVO(String texto1) {
		super();
		this.texto1 = texto1;
	}
	
	public LinhaReportVO(){		
	}
	
	public LinhaReportVO(String texto1, String texto2, String texto3,
			String texto4, String texto5, String texto6, Date data) {
		super();
		this.texto1 = texto1;
		this.texto2 = texto2;
		this.texto3 = texto3;
		this.texto4 = texto4;
		this.texto5 = texto5;
		this.texto6 = texto6;
		this.data = data;
	}
	
	public LinhaReportVO(String texto1, byte[] imagem) {
		super();
		this.texto1 = texto1;
		this.imagem = imagem;
	}
	
	public String getTexto1() {
		return texto1;
	}
	public void setTexto1(String texto1) {
		this.texto1 = texto1;
	}
	public String getTexto2() {
		return texto2;
	}
	public void setTexto2(String texto2) {
		this.texto2 = texto2;
	}
	public String getTexto3() {
		return texto3;
	}
	public void setTexto3(String texto3) {
		this.texto3 = texto3;
	}
	public String getTexto4() {
		return texto4;
	}
	public void setTexto4(String texto4) {
		this.texto4 = texto4;
	}
	public String getTexto5() {
		return texto5;
	}
	public void setTexto5(String texto5) {
		this.texto5 = texto5;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}

	public void setBool(Boolean bool) {
		this.bool = bool;
	}

	public Boolean getBool() {
		return bool;
	}

	public void setTexto6(String texto6) {
		this.texto6 = texto6;
	}

	public String getTexto6() {
		return texto6;
	}

	public void setNumero1(BigDecimal numero1) {
		this.numero1 = numero1;
	}

	public BigDecimal getNumero1() {
		return numero1;
	}

	public void setNumero2(BigDecimal numero2) {
		this.numero2 = numero2;
	}

	public BigDecimal getNumero2() {
		return numero2;
	}

	public void setNumero3(BigDecimal numero3) {
		this.numero3 = numero3;
	}

	public BigDecimal getNumero3() {
		return numero3;
	}

	public List<LinhaReportVO> getSubReport1() {
		return subReport1;
	}

	public List<LinhaReportVO> getSubReport2() {
		return subReport2;
	}

	public List<LinhaReportVO> getSubReport3() {
		return subReport3;
	}

	public List<LinhaReportVO> getSubReport4() {
		return subReport4;
	}

	public List<LinhaReportVO> getSubReport5() {
		return subReport5;
	}

	public List<LinhaReportVO> getSubReport6() {
		return subReport6;
	}

	public List<LinhaReportVO> getSubReport7() {
		return subReport7;
	}

	public List<LinhaReportVO> getSubReport8() {
		return subReport8;
	}
	
	public void setSubReport1(List<LinhaReportVO> subReport1) {
		this.subReport1 = subReport1;
	}

	public void setSubReport2(List<LinhaReportVO> subReport2) {
		this.subReport2 = subReport2;
	}

	public void setSubReport3(List<LinhaReportVO> subReport3) {
		this.subReport3 = subReport3;
	}

	public void setSubReport4(List<LinhaReportVO> subReport4) {
		this.subReport4 = subReport4;
	}

	public void setSubReport5(List<LinhaReportVO> subReport5) {
		this.subReport5 = subReport5;
	}

	public void setSubReport6(List<LinhaReportVO> subReport6) {
		this.subReport6 = subReport6;
	}

	public void setSubReport7(List<LinhaReportVO> subReport7) {
		this.subReport7 = subReport7;
	}

	public void setSubReport8(List<LinhaReportVO> subReport8) {
		this.subReport8 = subReport8;
	}
	
	public String getTexto7() {
		return texto7;
	}

	public String getTexto8() {
		return texto8;
	}

	public String getTexto9() {
		return texto9;
	}

	public void setTexto7(String texto7) {
		this.texto7 = texto7;
	}

	public void setTexto8(String texto8) {
		this.texto8 = texto8;
	}

	public void setTexto9(String texto9) {
		this.texto9 = texto9;
	}

	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}

	public byte[] getImagem() {
		return imagem;
	}

	public void setNumero4(Short numero4) {
		this.numero4 = numero4;
	}

	public Short getNumero4() {
		return numero4;
	}

	public enum Fields {

		TEXTO1("texto1"),
		TEXTO2("texto2"),
		NUMERO1("numero1"),
		NUMERO2("numero2"),
		NUMERO3("numero3"),
		NUMERO4("numero4"),
		NUMERO5("numero5"),
		NUMERO6("numero6"),
		NUMERO7("numero7"),
		NUMERO8("numero8"),
		NUMERO9("numero9"),
		NUMERO10("numero10"),
		NUMERO11("numero11"),
		NUMERO12("numero12"),
		DOMINIO1("dominio1"),
		DOMINIO2("dominio2"),
		DATA("data"),
		DATA1("data1"),
		OBJECT("object"),
		SIGLA_ESP("siglaEsp"),
		SEQ_ESP("seqEsp")
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Short getNumero5() {
		return numero5;
	}

	public void setNumero5(Short numero5) {
		this.numero5 = numero5;
	}

	public Date getData1() {
		return data1;
	}

	public void setData1(Date data1) {
		this.data1 = data1;
	}
	
	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Long getNumero6() {
		return numero6;
	}

	public void setNumero6(Long numero6) {
		this.numero6 = numero6;
	}

	public Integer getNumero7() {
		return numero7;
	}

	public void setNumero7(Integer numero7) {
		this.numero7 = numero7;
	}

	public DominioIndContaminacao getDominio1() {
		return dominio1;
	}

	public void setDominio1(DominioIndContaminacao dominio1) {
		this.dominio1 = dominio1;
	}

	public DominioSituacao getDominio2() {
		return dominio2;
	}

	public void setDominio2(DominioSituacao dominio2) {
		this.dominio2 = dominio2;
	}

	public Integer getNumero8() {
		return numero8;
	}

	public void setNumero8(Integer numero8) {
		this.numero8 = numero8;
	}

	public void setNumero9(String numero9) {
		this.numero9 = numero9;
	}

	public String getNumero9() {
		return numero9;
	}

	public Integer getNumero11() {
		return numero11;
	}

	public void setNumero11(Integer numero11) {
		this.numero11 = numero11;
	}	
		
	public Short getNumero12() {
		return numero12;
	}

	public void setNumero12(Short numero12) {
		this.numero12 = numero12;
	}
	
	public String getSiglaEsp() {
		return siglaEsp;
	}

	public void setSiglaEsp(String siglaEsp) {
		this.siglaEsp = siglaEsp;
	}

	public Short getSeqEsp() {
		return seqEsp;
	}

	public void setSeqEsp(Short seqEsp) {
		this.seqEsp = seqEsp;
	}
}