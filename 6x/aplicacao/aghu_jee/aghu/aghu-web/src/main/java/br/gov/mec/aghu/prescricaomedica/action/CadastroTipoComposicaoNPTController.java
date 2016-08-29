package br.gov.mec.aghu.prescricaomedica.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaTipoComposicoes;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultaTipoComposicoesVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroTipoComposicaoNPTController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9092956930324174799L;
	
	

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	private final String CADASTRO_TIPO_COMPOSICOES = "cadastroTipoComposicaoNPTList";
	
	private AfaTipoComposicoes tipoComposicoes = new AfaTipoComposicoes();
	private ConsultaTipoComposicoesVO consultaTipoComposicoesVO = new ConsultaTipoComposicoesVO();
	private Boolean producao = Boolean.FALSE;
	private boolean ativo = Boolean.TRUE;
	
	private enum CadastroTipoComposicaoNPTControllerExceptionCode implements BusinessExceptionCode {
		TIPO_COMPOSICAO_MS03;
	}

	@PostConstruct
	public void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar(){
		if(consultaTipoComposicoesVO.getIndProducao()){
			producao = consultaTipoComposicoesVO.getIndProducao();
		}
		if(consultaTipoComposicoesVO.getIndSituacao() != null){
			ativo = consultaTipoComposicoesVO.getIndSituacao().isAtivo();
		}
	}
	public String voltar(){
		limpar();
		return CADASTRO_TIPO_COMPOSICOES;
	}	
	
	private void limpar(){
		consultaTipoComposicoesVO = new ConsultaTipoComposicoesVO();
		tipoComposicoes = new AfaTipoComposicoes();
		producao = false;
		ativo = false;
	}

	public void gravar() throws ApplicationBusinessException{
		if(consultaTipoComposicoesVO.getSeq() != null){
			try{
				preInsert();
				prescricaoMedicaFacade.alterarAfaTipoComposicoes(tipoComposicoes);
				apresentarMsgNegocio(Severity.INFO, "TIPO_COMPOSICAO_ALTERADO_SUCESSO");
			}catch(BaseException e){
				ativo = true;
				apresentarExcecaoNegocio(e);
			}
		}
		else{
			try{
				preInsert();
				prescricaoMedicaFacade.gravarAfaTipoComposicoes(tipoComposicoes);
				apresentarMsgNegocio(Severity.INFO, "TIPO_COMPOSICAO_GRAVADO_SUCESSO");
			}catch(BaseException e){
				apresentarExcecaoNegocio(e);
			}
			limpar();
		}
	}

	private void preInsert() throws ApplicationBusinessException {
		if(consultaTipoComposicoesVO.getDescricao() != null){
			tipoComposicoes.setDescricao(consultaTipoComposicoesVO.getDescricao());
		}else{
			throw new ApplicationBusinessException(CadastroTipoComposicaoNPTControllerExceptionCode.TIPO_COMPOSICAO_MS03);
		}
		consultaTipoComposicoesVO.setIndProducao(producao == Boolean.TRUE ? Boolean.TRUE :Boolean.FALSE);
		consultaTipoComposicoesVO.setIndSituacao(ativo == true ? DominioSituacao.A : DominioSituacao.I);
		if(consultaTipoComposicoesVO.getSeq() != null){
			tipoComposicoes.setSeq(consultaTipoComposicoesVO.getSeq());
		}
		if(consultaTipoComposicoesVO.getOrdem() != null){
			tipoComposicoes.setOrdem(consultaTipoComposicoesVO.getOrdem());
		} 
		tipoComposicoes.setIndProducao(consultaTipoComposicoesVO.getIndProducao());
		tipoComposicoes.setIndSituacao(consultaTipoComposicoesVO.getIndSituacao());
	}
	/**
	 * Lançar mensagem, caso o usuario desmarque o checkbox, ativo
	 * ON04
	 * @throws ApplicationBusinessException
	 */
	public void validaAlterarAtivo() {
		if(!ativo && consultaTipoComposicoesVO.getSeq()!=null){
			apresentarMsgNegocio("TIPO_COMPOSICAO_MS01");
		}
	}
	
	//GET e SET

	public ConsultaTipoComposicoesVO getConsultaTipoComposicoesVO() {
		return consultaTipoComposicoesVO;
	}

	public void setConsultaTipoComposicoesVO(
			ConsultaTipoComposicoesVO consultaTipoComposicoesVO) {
		this.consultaTipoComposicoesVO = consultaTipoComposicoesVO;
	}

	public AfaTipoComposicoes getTipoComposicoes() {
		return tipoComposicoes;
	}

	public void setTipoComposicoes(AfaTipoComposicoes tipoComposicoes) {
		this.tipoComposicoes = tipoComposicoes;
	}

	public boolean isProducao() {
		return producao;
	}

	public void setProducao(boolean producao) {
		this.producao = producao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}	
}