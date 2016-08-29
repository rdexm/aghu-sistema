package br.gov.mec.aghu.sicon.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.sicon.vo.AutorizacaoFornecimentoVO;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class ManterAfContratoController extends ActionController {

	private static final long serialVersionUID = -2702566153580730978L;
	
	private static final String PAGE_MANTER_CONTRATO_AUTOMATICO = "manterContratoAutomatico";

	@EJB
	private ISiconFacade siconFacade;

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;
	
	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;
	
	private ScoContrato contrato;
	private Integer seqContrato;
	private Long nrContrato;
	private boolean contratoJaEnviado;
	private List<AutorizacaoFornecimentoVO> listaAutorizacaoFornecimentoVO;
	private AutorizacaoFornecimentoVO autorizacaoFornecimentoVO;

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * Método de inicialização da tela
	 */
	public void iniciar() {
	 
		if (this.seqContrato != null) {
			try {
				this.contrato = this.siconFacade.getContrato(this.seqContrato);
				setNrContrato(contrato.getNrContrato());
				
				permiteEditarVinculoAFContrato(contrato);
			} catch (BaseException e) {
				setContratoJaEnviado(true);
				apresentarExcecaoNegocio(e);
			}
		}

		// Busca lista de autorizações de fornecimento vinculadas ao Contrato e
		// também as autorizações ainda não vinculadas ao contrato, mas relacionadas ao
		// fornecedor e licitação deste contrato
		listaAutorizacaoFornecimentoVO = this.siconFacade
				.listarAfTheFornLicContrato(contrato);

		if(!isContratoJaEnviado()){
			// Por padrão, o campo 'Vincular ao Contrato' é inicializado com 'Sim' para todas as AF's
			for (AutorizacaoFornecimentoVO aFVO : listaAutorizacaoFornecimentoVO) {
				aFVO.setVincularAoContrato(DominioSimNao.S);
			}
		}

	
	}
	
	private void permiteEditarVinculoAFContrato(ScoContrato contrato){
		if (verificaContratoContabilizado(contrato) || verificaContratoEnviado(contrato)){
			setContratoJaEnviado(true);
		} else {
			setContratoJaEnviado(false);
		}
	}
	
	private boolean verificaContratoContabilizado(ScoContrato contrato){
		boolean contabilizado;

		if (custosSigCadastrosBasicosFacade.verificarContratoContabilizado(contrato)){
			contabilizado = true;
		} else {
			contabilizado = false;
		}
		
		return contabilizado;
	}
	
	private boolean verificaContratoEnviado(ScoContrato contrato){
		boolean enviado;
		
		List<ScoAditContrato> scoAditContrato;
		scoAditContrato = siconFacade.listarAditivosByContrato(contrato);

		if ("E".equals(contrato.getSituacao().toString()) && !scoAditContrato.isEmpty()) {
			enviado = true;
		} else {
			enviado = false;
		}
		
		return enviado;
	}

	public void gravar() {
		try {

			siconFacade.gravarAfContratos(listaAutorizacaoFornecimentoVO,
					contrato);

			listaAutorizacaoFornecimentoVO = this.siconFacade
					.listarAfTheFornLicContrato(contrato);
			
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_ALTERACAO_AFS_COM_SUCESSO");		
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}


	}
	
	public String voltar() throws BaseException{
		return PAGE_MANTER_CONTRATO_AUTOMATICO;
	}

	public String getPac() {
		if (contrato != null && this.contrato.getLicitacao() != null) {
			return contrato.getLicitacao().getNumero().toString() + " - "
					+ contrato.getLicitacao().getDescricao();
		} else {
			return null;
		}
	}

	/**
	 * Gets the cpf cnpj.
	 * 
	 * @return the cpf cnpj
	 */
	public String getCpfCnpj() {
		if (this.contrato != null && this.contrato.getFornecedor() != null
				&& this.contrato.getFornecedor().getCgc() != null) {
			return CoreUtil
					.formatarCNPJ(this.contrato.getFornecedor().getCgc());
		} else if (this.contrato != null
				&& this.contrato.getFornecedor() != null
				&& this.contrato.getFornecedor().getCpf() != null) {
			return CoreUtil.formataCPF(this.contrato.getFornecedor().getCpf());
		}
		return "";
	}

	public boolean materialPossuiCodSiasg(ScoMaterial material) {
		return this.cadastrosBasicosSiconFacade.pesquisarMaterialSicon(null, material, DominioSituacao.A, null) != null;
	}
	
	public boolean servicoPossuiCodSiasg(ScoServico servico) {
		return this.cadastrosBasicosSiconFacade.pesquisarServicoSicon(null, servico, DominioSituacao.A, null) != null;
	}
	
	public ScoContrato getContrato() {
		return contrato;
	}

	public void setContrato(ScoContrato contrato) {
		this.contrato = contrato;
	}

	public Integer getSeqContrato() {
		return seqContrato;
	}

	public void setSeqContrato(Integer seqContrato) {
		this.seqContrato = seqContrato;
	}

	public Long getNrContrato() {
		return nrContrato;
	}

	public void setNrContrato(Long nrContrato) {
		this.nrContrato = nrContrato;
	}

	public boolean isContratoJaEnviado() {
		return contratoJaEnviado;
	}

	public void setContratoJaEnviado(boolean contratoJaEnviado) {
		this.contratoJaEnviado = contratoJaEnviado;
	}

	public List<AutorizacaoFornecimentoVO> getListaAutorizacaoFornecimentoVO() {
		return listaAutorizacaoFornecimentoVO;
	}

	public void setListaAutorizacaoFornecimentoVO(
			List<AutorizacaoFornecimentoVO> listaAutorizacaoFornecimentoVO) {
		this.listaAutorizacaoFornecimentoVO = listaAutorizacaoFornecimentoVO;
	}

	public AutorizacaoFornecimentoVO getAutorizacaoFornecimentoVO() {
		return autorizacaoFornecimentoVO;
	}

	public void setAutorizacaoFornecimentoVO(
			AutorizacaoFornecimentoVO autorizacaoFornecimentoVO) {
		this.autorizacaoFornecimentoVO = autorizacaoFornecimentoVO;
	}

}