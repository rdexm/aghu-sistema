package br.gov.mec.aghu.patrimonio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class EncaminharSolicitacaoAnaliseTecnicaController extends
		ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4317044114744962555L;

	@EJB
	private IPatrimonioFacade patrimonioFacade;

	@EJB
	private IPermissionService permissionService;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private RapServidores rapServidor;
	
	private RapServidores servidorLogado;

	private PtmAreaTecAvaliacao areaTecnicaAvaliacao;
	
	private List<RapServidores> listSuperiorResponsavel;
	
	private AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO;

	private boolean permissaoChefeArea;

	private boolean permissaoChefiaPatrimonio;
	
	private boolean habilitaSuggestionArea;
	
	private boolean habilitaSuggestionResponsavel;
	
	private List<PtmAreaTecAvaliacao> listaAreaTecnica;

	@PostConstruct
	public void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
		servidorLogado = servidorLogadoFacade.obterServidorLogado();
		areaTecnicaAvaliacao = null;
		rapServidor = null;
		
		permissaoChefeArea = this.permissionService.usuarioTemPermissao(
				this.obterLoginUsuarioLogado(), "chefeAreaTecnicaAvaliacao",
				"executar");
		permissaoChefiaPatrimonio = this.permissionService.usuarioTemPermissao(
				this.obterLoginUsuarioLogado(),
				"chefiaPatrimonioAreaTecnicaAvaliacao", "executar");
		
		if(permissaoChefeArea){
			
			try {
				
				listSuperiorResponsavel = this.patrimonioFacade
						.pesquisarResponsavelTecnicoAreaTecnicaAvaliacao(aceiteTecnicoParaSerRealizadoVO);
			} catch (ApplicationBusinessException e) {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ENVIAR_PARAMETRO",
						e.getMessage());
			}
			
			if(listSuperiorResponsavel != null && !listSuperiorResponsavel.isEmpty()){
				habilitaSuggestionResponsavel = false;
			}else{
				habilitaSuggestionResponsavel = true;
			}
			
			try {
				listaAreaTecnica = this.patrimonioFacade.listarAreaTecnicaAvaliacaoAbaixoCentroCusto(aceiteTecnicoParaSerRealizadoVO);
				
				if(listaAreaTecnica != null && !listaAreaTecnica.isEmpty()){
					habilitaSuggestionArea = false;
				}else{
					habilitaSuggestionArea = true;
				}
			}catch(ApplicationBusinessException e){
				apresentarMsgNegocio(Severity.ERROR, e.getMessage());
			}
		}
	}

	/**
	 * SuggestionBox Área Técnica de Avaliação.
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<PtmAreaTecAvaliacao> listaAreaTecnicaAvaliacao(
			String objPesquisa) {

		if (isPermissaoChefiaPatrimonio()) {
			return returnSGWithCount(
					this.patrimonioFacade
							.pesquisarAreaTecnicaAvaliacao(objPesquisa),
					this.patrimonioFacade
							.pesquisarAreaTecnicaAvaliacaoCount(objPesquisa));
		} else if (isPermissaoChefeArea()) {
			List<Integer> listaSeqAreaTecnica = new ArrayList<Integer>();
			for (PtmAreaTecAvaliacao areaTec : listaAreaTecnica) {
				listaSeqAreaTecnica.add(areaTec.getSeq());
			}
			
			return returnSGWithCount(
					this.patrimonioFacade
							.pesquisarAreaTecnicaAvaliacaoAssociadoCentroCusto(objPesquisa, listaSeqAreaTecnica),
					this.patrimonioFacade
							.pesquisarAreaTecnicaAvaliacaoAssociadoCentroCustoCount(objPesquisa, listaSeqAreaTecnica));
		}
		return null;
	}

	/**
	 * SuggestionBox Responsável Técnico.
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<RapServidores> listarResponsavelTecnico(String objPesquisa) {
		if(isPermissaoChefeArea()){
			return consultarSuggestionPm01(objPesquisa); 
		} else if(isPermissaoChefiaPatrimonio()){
			return returnSGWithCount(
					this.patrimonioFacade
					.pesquisarResponsavelTecnico(objPesquisa),
					this.patrimonioFacade
					.pesquisarResponsavelTecnicoCount(objPesquisa));
		}
		
		return null;
	}

	private List<RapServidores> consultarSuggestionPm01(String objPesquisa) {
		if (objPesquisa != null && StringUtils.isNotBlank(objPesquisa)){
			if(CoreUtil.isNumeroInteger(objPesquisa) || CoreUtil.isNumeroShort(objPesquisa)) {
				if( listSuperiorResponsavel.get(0).getId().getMatricula().equals( (Integer.valueOf(objPesquisa) ))
					|| listSuperiorResponsavel.get(0).getId().getVinCodigo().equals((Short.valueOf(objPesquisa) ))
						){
					return returnSGWithCount(listSuperiorResponsavel, Long.valueOf(listSuperiorResponsavel.size()));
				}
			}
			if(StringUtils.isAlphanumeric(objPesquisa)) {
				if(listSuperiorResponsavel.get(0).getPessoaFisica().getNome().contains(objPesquisa)){
					return returnSGWithCount(listSuperiorResponsavel, Long.valueOf(listSuperiorResponsavel.size()));
				}
			}
			return returnSGWithCount(new ArrayList<RapServidores>(), 0L);
		}	
		return returnSGWithCount(listSuperiorResponsavel, Long.valueOf(listSuperiorResponsavel.size()));
	}
	
	public String encaminhar(){
		
		if(areaTecnicaAvaliacao != null && rapServidor == null){
			if(isPermissaoChefeArea() || isPermissaoChefiaPatrimonio()){
				
				try {
					this.patrimonioFacade.enviarEmailSolicitacaoTecnicaAnalise(areaTecnicaAvaliacao, rapServidor, aceiteTecnicoParaSerRealizadoVO, false,
							permissaoChefiaPatrimonio);
					closeDialog("modalEncaminharSolicitacaoAnaliseTecWG");
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOLICITACAO_ENVIADA_SUCESSO");
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				} catch (Exception e) {
					apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_ENVIAR_EMAIL",
							e.getMessage());
				}
			}
			
		}else if(areaTecnicaAvaliacao == null && rapServidor != null){
			if(isPermissaoChefiaPatrimonio()){
				try {
					this.patrimonioFacade.enviarEmailSolicitacaoTecnicaAnalise(areaTecnicaAvaliacao, rapServidor, aceiteTecnicoParaSerRealizadoVO, false,
							permissaoChefiaPatrimonio);
					closeDialog("modalEncaminharSolicitacaoAnaliseTecWG");
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOLICITACAO_ENVIADA_SUCESSO");
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				} catch (Exception e) {
					apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_ENVIAR_EMAIL",
							e.getMessage());
				}
			}else if(isPermissaoChefeArea()){
				try {
					this.patrimonioFacade.enviarEmailSolicitacaoTecnicaAnalise(areaTecnicaAvaliacao, rapServidor, aceiteTecnicoParaSerRealizadoVO, true,
							permissaoChefiaPatrimonio);
					closeDialog("modalEncaminharSolicitacaoAnaliseTecWG");
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOLICITACAO_ENVIADA_SUCESSO");
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				} catch (Exception e) {
					apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_ENVIAR_EMAIL",
							e.getMessage());
				}
			}
		
		}else if(areaTecnicaAvaliacao != null && rapServidor != null){
			this.apresentarMsgNegocio(Severity.ERROR,
					"MENSAGEM_SOLICITACAO_ENVIADA_ERRO");
		}else if(areaTecnicaAvaliacao == null && rapServidor == null){
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SOLICITACAO_NAO_ENVIADA");
		}
		areaTecnicaAvaliacao = new PtmAreaTecAvaliacao();
		rapServidor = new RapServidores();
		
		return null;
	}

	/**
	 * Getters and Setters
	 * 
	 * @return
	 */
	public RapServidores getRapServidor() {
		return rapServidor;
	}

	public void setRapServidor(RapServidores rapServidor) {
		this.rapServidor = rapServidor;
	}

	public PtmAreaTecAvaliacao getAreaTecnicaAvaliacao() {
		return areaTecnicaAvaliacao;
	}

	public void setAreaTecnicaAvaliacao(PtmAreaTecAvaliacao areaTecnicaAvaliacao) {
		this.areaTecnicaAvaliacao = areaTecnicaAvaliacao;
	}

	public boolean isPermissaoChefeArea() {
		return permissaoChefeArea;
	}

	public void setPermissaoChefeArea(boolean permissaoChefeArea) {
		this.permissaoChefeArea = permissaoChefeArea;
	}

	public boolean isPermissaoChefiaPatrimonio() {
		return permissaoChefiaPatrimonio;
	}

	public void setPermissaoChefiaPatrimonio(boolean permissaoChefiaPatrimonio) {
		this.permissaoChefiaPatrimonio = permissaoChefiaPatrimonio;
	}

	public boolean isHabilitaSuggestionArea() {
		return habilitaSuggestionArea;
	}

	public void setHabilitaSuggestionArea(boolean habilitaSuggestionArea) {
		this.habilitaSuggestionArea = habilitaSuggestionArea;
	}

	public boolean isHabilitaSuggestionResponsavel() {
		return habilitaSuggestionResponsavel;
	}

	public void setHabilitaSuggestionResponsavel(
			boolean habilitaSuggestionResponsavel) {
		this.habilitaSuggestionResponsavel = habilitaSuggestionResponsavel;
	}

	public AceiteTecnicoParaSerRealizadoVO getAceiteTecnicoParaSerRealizadoVO() {
		return aceiteTecnicoParaSerRealizadoVO;
	}

	public void setAceiteTecnicoParaSerRealizadoVO(
			AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO) {
		this.aceiteTecnicoParaSerRealizadoVO = aceiteTecnicoParaSerRealizadoVO;
	}

	public List<RapServidores> getListSuperiorResponsavel() {
		return listSuperiorResponsavel;
	}

	public void setListSuperiorResponsavel(
			List<RapServidores> listSuperiorResponsavel) {
		this.listSuperiorResponsavel = listSuperiorResponsavel;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}
	
}
