package br.gov.mec.aghu.compras.vo;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@SuppressWarnings({"PMD.AghuTooManyMethods", "ucd"})
public class SolicitacaoDeComprasVO implements List<SolicitacaoDeComprasVO> {
	
	private Integer nroSolicitacao;
	private Date dataSltc;
	private Date dataAutz;
	private Integer cCustoRqsc;
	private String cCustoRqscDesc;
	private Integer cCustoAplc;
	private String cCustoAplcDesc;
	private Integer codigo;
	private String nomeMaterial;
	private String unid;
	private Long qtdSltd;
	private Short diasDur;
	private Long qtdAprov;
	private Integer convenio1;
	private Integer natureza1;
	private Byte natureza2;
	private String descricaoSolicitacao;
	private String descricaoCatalogo;
	private String pacNome;	
	private String aplicacao;
	private Integer prontuario;
	private String justificativa;
	private String urgencia;
	private String prioridade;
	private String exclusividade;
	private String nomeSolicitante;
	private String nomeAutorizador;
	private Integer cartaoPontoAutorizador;
	private Integer cartaoPontoSolicitante;
	private Integer nroInvestimento;
	private Integer nroProjeto;
	private String natureza3;
	private String dataAlt;
	private Short serVinCodigoAlterada;
	private Integer serMatriculaAlterada;
	private String descricaoAutTec;
	private String convenio2;
	private BigDecimal valorUnitPrevisto;
	private Date dataLiber;
	private Short serVinCodigoOrcamento;
	private Integer serMatriculaOrcamento;
	private Integer ramalSolic;
	private Integer ramalAut;
	
	private String newAplicacao;
	private String newDataAlt;
	private String responsavel;
	private BigDecimal totalValorPrevisto;
	private String responsavel2;
	private Boolean possuiAnexo;
	
	private String nroAF;
	private Integer ultimoNR;
	private Date dataEntrada;
	private String end;
	
	private Date dataAnalise;
	
	private Date dtMaxAtendimento;
	
	/*	Getters and Setters */

	public String getcCustoRqscDesc() {
		return cCustoRqscDesc;
	}
	public void setcCustoRqscDesc(String cCustoRqscDesc) {
		this.cCustoRqscDesc = cCustoRqscDesc;
	}
	public String getNomeMaterial() {
		return nomeMaterial;
	}
	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}
	public String getUnid() {
		return unid;
	}
	public void setUnid(String unid) {
		this.unid = unid;
	}

	public String getDescricaoSolicitacao() {
		return descricaoSolicitacao;
	}
	public void setDescricaoSolicitacao(String descricaoSolicitacao) {
		this.descricaoSolicitacao = descricaoSolicitacao;
	}
	public String getDescricaoCatalogo() {
		return descricaoCatalogo;
	}
	public void setDescricaoCatalogo(String descricaoCatalogo) {
		this.descricaoCatalogo = descricaoCatalogo;
	}
	public String getAplicacao() {
		return aplicacao;
	}
	public void setAplicacao(String aplicacao) {
		this.aplicacao = aplicacao;
	}
	public String getJustificativa() {
		return justificativa;
	}
	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}
	public String getUrgencia() {
		return urgencia;
	}
	public void setUrgencia(String urgencia) {
		this.urgencia = urgencia;
	}
	public String getNomeSolicitante() {
		return nomeSolicitante;
	}
	public void setNomeSolicitante(String nomeSolicitante) {
		this.nomeSolicitante = nomeSolicitante;
	}
	public String getNomeAutorizador() {
		return nomeAutorizador;
	}
	public void setNomeAutorizador(String nomeAutorizador) {
		this.nomeAutorizador = nomeAutorizador;
	}

	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getcCustoAplcDesc() {
		return cCustoAplcDesc;
	}
	public void setcCustoAplcDesc(String cCustoAplcDesc) {
		this.cCustoAplcDesc = cCustoAplcDesc;
	}
	public Integer getNroSolicitacao() {
		return nroSolicitacao;
	}
	public void setNroSolicitacao(Integer nroSolicitacao) {
		this.nroSolicitacao = nroSolicitacao;
	}
	public Integer getcCustoRqsc() {
		return cCustoRqsc;
	}
	public void setcCustoRqsc(Integer cCustoRqsc) {
		this.cCustoRqsc = cCustoRqsc;
	}
	public Integer getcCustoAplc() {
		return cCustoAplc;
	}
	public void setcCustoAplc(Integer cCustoAplc) {
		this.cCustoAplc = cCustoAplc;
	}
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public Long getQtdSltd() {
		return qtdSltd;
	}
	public void setQtdSltd(Long qtdSltd) {
		this.qtdSltd = qtdSltd;
	}
	public Short getDiasDur() {
		return diasDur;
	}
	public void setDiasDur(Short diasDur) {
		this.diasDur = diasDur;
	}
	public Long getQtdAprov() {
		return qtdAprov;
	}
	public void setQtdAprov(Long qtdAprov) {
		this.qtdAprov = qtdAprov;
	}
	public Integer getConvenio1() {
		return convenio1;
	}
	public void setConvenio1(Integer convenio1) {
		this.convenio1 = convenio1;
	}
	public Integer getCartaoPontoAutorizador() {
		return cartaoPontoAutorizador;
	}
	public void setCartaoPontoAutorizador(Integer cartaoPontoAutorizador) {
		this.cartaoPontoAutorizador = cartaoPontoAutorizador;
	}
	public Integer getNatureza1() {
		return natureza1;
	}
	public void setNatureza1(Integer natureza1) {
		this.natureza1 = natureza1;
	}
	public Byte getNatureza2() {
		return natureza2;
	}
	public void setNatureza2(Byte natureza2) {
		this.natureza2 = natureza2;
	}
	public Integer getNroInvestimento() {
		return nroInvestimento;
	}
	public void setNroInvestimento(Integer nroInvestimento) {
		this.nroInvestimento = nroInvestimento;
	}
	public String getPrioridade() {
		return prioridade;
	}
	public void setPrioridade(String prioridade) {
		this.prioridade = prioridade;
	}
	public Integer getNroProjeto() {
		return nroProjeto;
	}
	public void setNroProjeto(Integer nroProjeto) {
		this.nroProjeto = nroProjeto;
	}
	public String getNatureza3() {
		return natureza3;
	}
	public void setNatureza3(String natureza3) {
		this.natureza3 = natureza3;
	}
	public BigDecimal getValorUnitPrevisto() {
		return valorUnitPrevisto;
	}
	public void setValorUnitPrevisto(BigDecimal valorUnitPrevisto) {
		this.valorUnitPrevisto = valorUnitPrevisto;
	}
	public String getConvenio2() {
		return convenio2;
	}
	public void setConvenio2(String convenio2) {
		this.convenio2 = convenio2;
	}

	public Integer getRamalSolic() {
		return ramalSolic;
	}
	public void setRamalSolic(Integer ramalSolic) {
		this.ramalSolic = ramalSolic;
	}
	public Integer getRamalAut() {
		return ramalAut;
	}
	public void setRamalAut(Integer ramalAut) {
		this.ramalAut = ramalAut;
	}
	public String getNroAF() {
		return nroAF;
	}
	public void setNroAF(String nroAF) {
		this.nroAF = nroAF;
	}
	public Integer getUltimoNR() {
		return ultimoNR;
	}
	public void setUltimoNR(Integer ultimoNR) {
		this.ultimoNR = ultimoNR;
	}
	public String getDataAlt() {
		return dataAlt;
	}
	public void setDataAlt(String dataAlt) {
		this.dataAlt = dataAlt;
	}
	public Date getDataSltc() {
		return dataSltc;
	}
	public void setDataSltc(Date dataSltc) {
		this.dataSltc = dataSltc;
	}
	public Date getDataAutz() {
		return dataAutz;
	}
	public void setDataAutz(Date dataAutz) {
		this.dataAutz = dataAutz;
	}
	public Date getDataLiber() {
		return dataLiber;
	}
	public void setDataLiber(Date dataLiber) {
		this.dataLiber = dataLiber;
	}
	public Date getDataEntrada() {
		return dataEntrada;
	}
	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}
	public String getPacNome() {
		return pacNome;
	}
	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public Short getSerVinCodigoAlterada() {
		return serVinCodigoAlterada;
	}
	public void setSerVinCodigoAlterada(Short serVinCodigoAlterada) {
		this.serVinCodigoAlterada = serVinCodigoAlterada;
	}
	public Integer getSerMatriculaAlterada() {
		return serMatriculaAlterada;
	}
	public void setSerMatriculaAlterada(Integer serMatriculaAlterada) {
		this.serMatriculaAlterada = serMatriculaAlterada;
	}

	public Short getSerVinCodigoOrcamento() {
		return serVinCodigoOrcamento;
	}
	public void setSerVinCodigoOrcamento(Short serVinCodigoOrcamento) {
		this.serVinCodigoOrcamento = serVinCodigoOrcamento;
	}
	public Integer getSerMatriculaOrcamento() {
		return serMatriculaOrcamento;
	}
	public void setSerMatriculaOrcamento(Integer serMatriculaOrcamento) {
		this.serMatriculaOrcamento = serMatriculaOrcamento;
	}
	public String getNewAplicacao() {
		return newAplicacao;
	}
	public void setNewAplicacao(String newAplicacao) {
		this.newAplicacao = newAplicacao;
	}
	
	public String getNewDataAlt() {
		return newDataAlt;
	}
	public void setNewDataAlt(String newDataAlt) {
		this.newDataAlt = newDataAlt;
	}
	public String getResponsavel() {
		return responsavel;
	}
	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}
	
	public BigDecimal getTotalValorPrevisto() {
		return totalValorPrevisto;
	}
	public void setTotalValorPrevisto(BigDecimal totalValorPrevisto) {
		this.totalValorPrevisto = totalValorPrevisto;
	}
	
	public String getResponsavel2() {
		return responsavel2;
	}
	public void setResponsavel2(String responsavel2) {
		this.responsavel2 = responsavel2;
	}
	public String getDescricaoAutTec() {
		return descricaoAutTec;
	}
	public void setDescricaoAutTec(String descricaoAutTec) {
		this.descricaoAutTec = descricaoAutTec;
	}
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Iterator<SolicitacaoDeComprasVO> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean add(SolicitacaoDeComprasVO e) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean addAll(Collection<? extends SolicitacaoDeComprasVO> c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean addAll(int index,
			Collection<? extends SolicitacaoDeComprasVO> c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public SolicitacaoDeComprasVO get(int index) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SolicitacaoDeComprasVO set(int index, SolicitacaoDeComprasVO element) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void add(int index, SolicitacaoDeComprasVO element) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public SolicitacaoDeComprasVO remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public ListIterator<SolicitacaoDeComprasVO> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ListIterator<SolicitacaoDeComprasVO> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<SolicitacaoDeComprasVO> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}
	public String getExclusividade() {
		return exclusividade;
	}
	public void setExclusividade(String exclusividade) {
		this.exclusividade = exclusividade;
	}
		
	public Integer getCartaoPontoSolicitante() {
		return cartaoPontoSolicitante;
	}
	public void setCartaoPontoSolicitante(Integer cartaoPontoSolicitante) {
		this.cartaoPontoSolicitante = cartaoPontoSolicitante;
	}
	public Date getDataAnalise() {
		return dataAnalise;
	}
	public void setDataAnalise(Date dataAnalise) {
		this.dataAnalise = dataAnalise;
	}
	public Date getDtMaxAtendimento() {
		return dtMaxAtendimento;
	}
	public void setDtMaxAtendimento(Date dtMaxAtendimento) {
		this.dtMaxAtendimento = dtMaxAtendimento;
	}
	public Boolean getPossuiAnexo() {
		return possuiAnexo;
	}
	public void setPossuiAnexo(Boolean possuiAnexo) {
		this.possuiAnexo = possuiAnexo;
	}
}
