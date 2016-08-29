package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.ConsultaProgramacaoEntregaItemVO;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedido;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedidoId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class ListarItensAutorizacaoFornecimentoController extends
		ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ListarItensAutorizacaoFornecimentoController.class);

	private static final String PESQUISAR_PLANJ_PROG_ENTREGA_ITENS_AF = "compras-programacaoGeral";

	/**
	 * 
	 */
	private static final long serialVersionUID = -4305412613061953416L;

	private ConsultaProgramacaoEntregaItemVO consultaProgramacaoEntregaItemVO;
	private Date dtNecessidadeHCPA;
	private Date dtLibPlanejamento;
	private Date dtAssinatura;
	private Date dtEnvioForn;
	private String material;
	private String fornecedor;
	private boolean btProgramacaoPendente;
	private Integer numeroAf;
	private Integer numeroComplemento;
	private List<ConsultaProgramacaoEntregaItemVO> lista;
	private Integer seq;

	private ConsultaProgramacaoEntregaItemVO selecionado = new ConsultaProgramacaoEntregaItemVO();

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;

	public void inicio() {
	 

	 

		LOG.info("Iniciou action controller: ListarItensAutorizacaoFornecimentoController");
		try {
			this.lista = null;
			limpa();
			if (btProgramacaoPendente) {
				setLista(autFornecimentoFacade.obterProgramacaoPendente(
						numeroAf, numeroComplemento));
			} else {
				setLista(autFornecimentoFacade.obterProgramacaoGeral(numeroAf,
						numeroComplemento));
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}
	

	private void limpa() {
		this.dtNecessidadeHCPA = null;
		this.dtLibPlanejamento = null;
		this.dtAssinatura = null;
		this.dtEnvioForn = null;
		this.material = null;
		this.fornecedor = null;
		this.seq = null;
	}

	/**
	 * @return the consultaProgramacaoEntregaItemVO
	 */
	public ConsultaProgramacaoEntregaItemVO getConsultaProgramacaoEntregaItemVO() {
		return consultaProgramacaoEntregaItemVO;
	}

	/**
	 * @param consultaProgramacaoEntregaItemVO
	 *            the consultaProgramacaoEntregaItemVO to set
	 */
	public void setConsultaProgramacaoEntregaItemVO(
			ConsultaProgramacaoEntregaItemVO consultaProgramacaoEntregaItemVO) {
		this.consultaProgramacaoEntregaItemVO = consultaProgramacaoEntregaItemVO;
	}

	/**
	 * @return the dtNecessidadeHCPA
	 */
	public Date getDtNecessidadeHCPA() {
		return dtNecessidadeHCPA;
	}

	/**
	 * @param dtNecessidadeHCPA
	 *            the dtNecessidadeHCPA to set
	 */
	public void setDtNecessidadeHCPA(Date dtNecessidadeHCPA) {
		this.dtNecessidadeHCPA = dtNecessidadeHCPA;
	}

	/**
	 * @return the dtLibPlanejamento
	 */
	public Date getDtLibPlanejamento() {
		return dtLibPlanejamento;
	}

	/**
	 * @param dtLibPlanejamento
	 *            the dtLibPlanejamento to set
	 */
	public void setDtLibPlanejamento(Date dtLibPlanejamento) {
		this.dtLibPlanejamento = dtLibPlanejamento;
	}

	/**
	 * @return the dtAssinatura
	 */
	public Date getDtAssinatura() {
		return dtAssinatura;
	}

	/**
	 * @param dtAssinatura
	 *            the dtAssinatura to set
	 */
	public void setDtAssinatura(Date dtAssinatura) {
		this.dtAssinatura = dtAssinatura;
	}

	/**
	 * @return the dtEnvioForn
	 */
	public Date getDtEnvioForn() {
		return dtEnvioForn;
	}

	/**
	 * @param dtEnvioForn
	 *            the dtEnvioForn to set
	 */
	public void setDtEnvioForn(Date dtEnvioForn) {
		this.dtEnvioForn = dtEnvioForn;
	}

	/**
	 * @return the material
	 */
	public String getMaterial() {
		return material;
	}

	/**
	 * @param material
	 *            the material to set
	 */
	public void setMaterial(String material) {
		this.material = material;
	}

	/**
	 * @return the razaoFornecedor
	 */
	public String getFornecedor() {
		return fornecedor;
	}

	/**
	 * @param Fornecedor
	 *            the Fornecedor to set
	 */
	public void setFornecedor(String Fornecedor) {
		this.fornecedor = Fornecedor;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @param numeroAf
	 *            the numeroAf to set
	 */
	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	/**
	 * @return the numeroAf
	 */
	public Integer getNumeroAf() {
		return numeroAf;
	}

	/**
	 * @param numeroComplemento
	 *            the numeroComplemento to set
	 */
	public void setNumeroComplemento(Integer numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}

	/**
	 * @return the numeroComplemento
	 */
	public Integer getNumeroComplemento() {
		return numeroComplemento;
	}

	/**
	 * @param autFornecimentoFacade
	 *            the autFornecimentoFacade to set
	 */
	public void setAutFornecimentoFacade(
			IAutFornecimentoFacade autFornecimentoFacade) {
		this.autFornecimentoFacade = autFornecimentoFacade;
	}

	/**
	 * @return the autFornecimentoFacade
	 */
	public IAutFornecimentoFacade getAutFornecimentoFacade() {
		return autFornecimentoFacade;
	}

	/**
	 * @param lista
	 *            the lista to set
	 */
	public void setLista(List<ConsultaProgramacaoEntregaItemVO> lista) {
		this.lista = lista;
	}

	/**
	 * @return the lista
	 */
	public List<ConsultaProgramacaoEntregaItemVO> getLista() {
		return lista;
	}

	public void selecionarItem() {
		if (this.selecionado.getSeq() != null) {
			for (ConsultaProgramacaoEntregaItemVO vo : this.lista) {
				if (vo.getSeq().equals(this.selecionado.getSeq())) {
					limpa();
					ScoAutorizacaoFornecedorPedido autorizacaoFornecedorPedido;
					setDtNecessidadeHCPA(vo.getDtNecessidadeHCPA());
					setDtLibPlanejamento(vo.getDtLibPlanejamento());
					setDtAssinatura(vo.getDtAssinatura());
					if (vo.getAfeAfnNumero() != null
							&& vo.getAfnNumero() != null) {
						autorizacaoFornecedorPedido = new ScoAutorizacaoFornecedorPedido();
						autorizacaoFornecedorPedido = autFornecimentoFacade
								.obterScoAutorizacaoFornecedorPedidoPorChavePrimaria(new ScoAutorizacaoFornecedorPedidoId(
										vo.getAfeAfnNumero(), vo.getAfnNumero()));
						if (autorizacaoFornecedorPedido != null) {
							setDtEnvioForn(autorizacaoFornecedorPedido
									.getDtEnvioFornecedor());
						}
					}
					setMaterial(vo.getCodigoMaterial() + " - "
							+ vo.getNomeMaterial());
					setFornecedor(vo.getNumeroFornecedor() + " - "
							+ vo.getRazaoSocialFornecedor());
					break;
				}
			}
		}
	}

	/**
	 * @param btProgramacaoPendente
	 *            the btProgramacaoPendente to set
	 */
	public void setBtProgramacaoPendente(boolean btProgramacaoPendente) {
		this.btProgramacaoPendente = btProgramacaoPendente;
	}

	/**
	 * @return the btProgramacaoPendente
	 */
	public boolean isBtProgramacaoPendente() {
		return btProgramacaoPendente;
	}

	public String voltar() {
		return PESQUISAR_PLANJ_PROG_ENTREGA_ITENS_AF;
	}

	/**
	 * @param seq
	 *            the seq to set
	 */
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	/**
	 * @return the seq
	 */
	public Integer getSeq() {
		return seq;
	}

	public ConsultaProgramacaoEntregaItemVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ConsultaProgramacaoEntregaItemVO selecionado) {
		this.selecionado = selecionado;
	}
}
