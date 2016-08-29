package br.gov.mec.aghu.patrimonio.cadastroapoio;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.model.PtmDescMotivoMovimentos;
import br.gov.mec.aghu.model.PtmSituacaoMotivoMovimento;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastraMotivosMovimentoPorSituacaoController extends ActionController {

	private static final long serialVersionUID = -3846799621353319128L;
	
	private static final String PAGE_PESQUISA_MOTIVOS_MOVIMENTO_POR_SITUACAO = "patrimonio-pesquisaMotivosMovimentoPorSituacao";
	
	private PtmDescMotivoMovimentos ptmDescMotivoMovimentos;
	private List<PtmSituacaoMotivoMovimento> ptmSituacaoMotivoMovimentoList;
	private Boolean edicaoAtiva;
	
	@EJB
	private IPatrimonioFacade patrimonio;
	
	@PostConstruct 
	public void init(){
		begin(conversation, true);
	}
	
	public void iniciar(){
		if(edicaoAtiva){
			this.ptmDescMotivoMovimentos = patrimonio.obterPtmDescMotivoMovimentos(ptmDescMotivoMovimentos);
		}else{
			limpar();
		}
		
		if(ptmSituacaoMotivoMovimentoList == null){
			ptmSituacaoMotivoMovimentoList = this.patrimonio.obterTodasSituacoesMotivoMovimento();
		}
	}
	
	public String gravar() throws ApplicationBusinessException {
		try {
			
			if(ptmDescMotivoMovimentos !=null){
				if(ptmDescMotivoMovimentos.getPtmSituacaoMotivoMovimento() == null){
					throw new ApplicationBusinessException(getBundle().getString("CAMPO_SITUACAO_OBRIGATORIO"), Severity.ERROR, new Object());
				}
			}
			
			if(edicaoAtiva){
				patrimonio.atualizarMotivoSituacaoDescricao(ptmDescMotivoMovimentos);
				apresentarMsgNegocio(Severity.INFO, "MOTIVO_ATUALIZADO_COM_SUCESSO", "");
			}else{
				patrimonio.persistirMotivoSituacaoDescricao(ptmDescMotivoMovimentos);
				apresentarMsgNegocio(Severity.INFO, "MOTIVO_GRAVADO_COM_SUCESSO", "");
			}
				
		}catch (ApplicationBusinessException e) {

			if(e.getMessage().equalsIgnoreCase(getBundle().getString("CAMPO_SITUACAO_OBRIGATORIO"))){
				apresentarMsgNegocio("situacao", Severity.ERROR, e.getMessage(), new Object());     
				return null;
			}
			
			if(edicaoAtiva){
				apresentarMsgNegocio(Severity.ERROR, "MOTIVO_ATUALIZADO_SEM_SUCESSO", ptmDescMotivoMovimentos.getDescricao());
			}else{
				apresentarMsgNegocio(Severity.ERROR, "MOTIVO_GRAVADO_SEM_SUCESSO", ptmDescMotivoMovimentos.getDescricao());
			}
			return null;
		} 
		return PAGE_PESQUISA_MOTIVOS_MOVIMENTO_POR_SITUACAO;
	}

	public void limpar(){
		ptmDescMotivoMovimentos = new PtmDescMotivoMovimentos();
		ptmDescMotivoMovimentos.setAtivo(Boolean.TRUE);
		ptmDescMotivoMovimentos.setJustificativaObrig(Boolean.TRUE);
		ptmSituacaoMotivoMovimentoList = null;
		edicaoAtiva = false;
	}
	
	public String cancelar(){
		limpar();
		return PAGE_PESQUISA_MOTIVOS_MOVIMENTO_POR_SITUACAO;
	}
	
	//Getters and setters
	
	public PtmDescMotivoMovimentos getPtmDescMotivoMovimentos() {
		return ptmDescMotivoMovimentos;
	}

	public void setPtmDescMotivoMovimentos(PtmDescMotivoMovimentos ptmDescMotivoMovimentos) {
		this.ptmDescMotivoMovimentos = ptmDescMotivoMovimentos;
	}

	public Boolean getEdicaoAtiva() {
		return edicaoAtiva;
	}

	public void setEdicaoAtiva(Boolean edicaoAtiva) {
		this.edicaoAtiva = edicaoAtiva;
	}

	public List<PtmSituacaoMotivoMovimento> getPtmSituacaoMotivoMovimentoList() {
		return ptmSituacaoMotivoMovimentoList;
	}

	public void setPtmSituacaoMotivoMovimentoList(
			List<PtmSituacaoMotivoMovimento> ptmSituacaoMotivoMovimentoList) {
		this.ptmSituacaoMotivoMovimentoList = ptmSituacaoMotivoMovimentoList;
	}
}