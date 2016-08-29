package br.gov.mec.aghu.patrimonio.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoDocumentoPatrimonio;
import br.gov.mec.aghu.dominio.DominioTipoProcessoPatrimonio;
import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmNotificacaoTecnica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ArquivosAnexosPesquisaFiltroVO implements BaseBean{
	//Linha - Correspondente a linha do campo no prototipo de tela para facilitar localização

		/**
	 * 
	 */
	private static final long serialVersionUID = -3181480885138682970L;
		//Linha 1
		private Integer nroAf;
		private Integer sc;
		private Long notaFiscal;
		private Integer esl;
		private PtmBemPermanentes patrimonio;
		//Linha 2
		private DominioTipoDocumentoPatrimonio tipoDocumento;
		private String descricaoTipoDocumento;
		private DominioTipoProcessoPatrimonio tipoProcesso;
		private ScoMaterial material;
		//Linha 3
		private String arquivo;
		private String descricaoArquivoAnexo;
		//Linha 4
		private RapServidores usuarioUltimaAlteracao;
		private RapServidores usuarioInclusao;
		//Linha 5
		private Date dtIniInclusao;
		private Date dtFimInclusao;
		private Date dtIniUltAlt;
		private Date dtFimUltAlt;
		//Linha 6
		private PtmItemRecebProvisorios recebimentoItem;
		private PtmNotificacaoTecnica notificacaoTecnica;
		private Integer nroSindicancia;
		//Linha 7
		private Integer numroBO;
		private Integer nroProcAdmPenalidade;
		private Integer nroRegularizacao;
		private Integer nroPedido;
		private Integer cnpj;
		//Linha 8
		private Integer nroProcBaixa;
		private Integer centroCusto;
		private Integer loteBaixa;
		private Integer aceiteTecnico;
		 
		private byte[] anexo;
 		
		public Integer getNroAf() {
			return nroAf;
		}
		public void setNroAf(Integer nroAf) {
			this.nroAf = nroAf;
		}
		public Integer getSc() {
			return sc;
		}
		public void setSc(Integer sc) {
			this.sc = sc;
		}
		public Long getNotaFiscal() {
			return notaFiscal;
		}
		public void setNotaFiscal(Long notaFiscal) {
			this.notaFiscal = notaFiscal;
		}
		public Integer getEsl() {
			return esl;
		}
		public void setEsl(Integer esl) {
			this.esl = esl;
		}
		public PtmBemPermanentes getPatrimonio() {
			return patrimonio;
		}
		public void setPatrimonio(PtmBemPermanentes patrimonio) {
			this.patrimonio = patrimonio;
		}
		public DominioTipoDocumentoPatrimonio getTipoDocumento() {
			return tipoDocumento;
		}
		public void setTipoDocumento(DominioTipoDocumentoPatrimonio tipoDocumento) {
			this.tipoDocumento = tipoDocumento;
		}
		public String getDescricaoTipoDocumento() {
			return descricaoTipoDocumento;
		}
		public void setDescricaoTipoDocumento(String descricaoTipoDocumento) {
			this.descricaoTipoDocumento = descricaoTipoDocumento;
		}
		public DominioTipoProcessoPatrimonio getTipoProcesso() {
			return tipoProcesso;
		}
		public void setTipoProcesso(DominioTipoProcessoPatrimonio tipoProcesso) {
			this.tipoProcesso = tipoProcesso;
		}
		public ScoMaterial getMaterial() {
			return material;
		}
		public void setMaterial(ScoMaterial material) {
			this.material = material;
		}
		public String getArquivo() {
			return arquivo;
		}
		public void setArquivo(String arquivo) {
			this.arquivo = arquivo;
		}
		public String getDescricaoArquivoAnexo() {
			return descricaoArquivoAnexo;
		}
		public void setDescricaoArquivoAnexo(String descricaoArquivoAnexo) {
			this.descricaoArquivoAnexo = descricaoArquivoAnexo;
		}
		public RapServidores getUsuarioUltimaAlteracao() {
			return usuarioUltimaAlteracao;
		}
		public void setUsuarioUltimaAlteracao(RapServidores usuarioUltimaAlteracao) {
			this.usuarioUltimaAlteracao = usuarioUltimaAlteracao;
		}
		public RapServidores getUsuarioInclusao() {
			return usuarioInclusao;
		}
		public void setUsuarioInclusao(RapServidores usuarioInclusao) {
			this.usuarioInclusao = usuarioInclusao;
		}
		public Date getDtIniInclusao() {
			return dtIniInclusao;
		}
		public void setDtIniInclusao(Date dtIniInclusao) {
			this.dtIniInclusao = dtIniInclusao;
		}
		public Date getDtFimInclusao() {
			return dtFimInclusao;
		}
		public void setDtFimInclusao(Date dtFimInclusao) {
			this.dtFimInclusao = dtFimInclusao;
		}
		public Date getDtIniUltAlt() {
			return dtIniUltAlt;
		}
		public void setDtIniUltAlt(Date dtIniUltAlt) {
			this.dtIniUltAlt = dtIniUltAlt;
		}
		public Date getDtFimUltAlt() {
			return dtFimUltAlt;
		}
		public void setDtFimUltAlt(Date dtFimUltAlt) {
			this.dtFimUltAlt = dtFimUltAlt;
		}
		public PtmItemRecebProvisorios getRecebimentoItem() {
			return recebimentoItem;
		}
		public void setRecebimentoItem(PtmItemRecebProvisorios recebimentoItem) {
			this.recebimentoItem = recebimentoItem;
		}
		public PtmNotificacaoTecnica getNotificacaoTecnica() {
			return notificacaoTecnica;
		}
		public void setNotificacaoTecnica(PtmNotificacaoTecnica notificacaoTecnica) {
			this.notificacaoTecnica = notificacaoTecnica;
		}
		public Integer getNroSindicancia() {
			return nroSindicancia;
		}
		public void setNroSindicancia(Integer nroSindicancia) {
			this.nroSindicancia = nroSindicancia;
		}
		public Integer getNumroBO() {
			return numroBO;
		}
		public void setNumroBO(Integer numroBO) {
			this.numroBO = numroBO;
		}
		public Integer getNroProcAdmPenalidade() {
			return nroProcAdmPenalidade;
		}
		public void setNroProcAdmPenalidade(Integer nroProcAdmPenalidade) {
			this.nroProcAdmPenalidade = nroProcAdmPenalidade;
		}
		public Integer getNroRegularizacao() {
			return nroRegularizacao;
		}
		public void setNroRegularizacao(Integer nroRegularizacao) {
			this.nroRegularizacao = nroRegularizacao;
		}
		public Integer getNroPedido() {
			return nroPedido;
		}
		public void setNroPedido(Integer nroPedido) {
			this.nroPedido = nroPedido;
		}
		public Integer getCnpj() {
			return cnpj;
		}
		public void setCnpj(Integer cnpj) {
			this.cnpj = cnpj;
		}
		public Integer getNroProcBaixa() {
			return nroProcBaixa;
		}
		public void setNroProcBaixa(Integer nroProcBaixa) {
			this.nroProcBaixa = nroProcBaixa;
		}
		public Integer getCentroCusto() {
			return centroCusto;
		}
		public void setCentroCusto(Integer centroCusto) {
			this.centroCusto = centroCusto;
		}
		public Integer getLoteBaixa() {
			return loteBaixa;
		}
		public void setLoteBaixa(Integer loteBaixa) {
			this.loteBaixa = loteBaixa;
		}
		public byte[] getAnexo() {
			return anexo;
		}
		public void setAnexo(byte[] anexo) {
			this.anexo = anexo;
		}
		public Integer getAceiteTecnico() {
			return aceiteTecnico;
		}
		public void setAceiteTecnico(Integer aceiteTecnico) {
			this.aceiteTecnico = aceiteTecnico;
		}
		
}
