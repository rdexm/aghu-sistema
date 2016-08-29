package br.gov.mec.aghu.patrimonio.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import com.google.common.base.Strings;

import br.gov.mec.aghu.dominio.DominioStatusNotificacaoTecnica;
import br.gov.mec.aghu.model.PtmNotificacaoTecnica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroNotificacaoTecnicaController extends ActionController{	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2685721217985914606L;
	
	private static final String PAGINA_LISTA_ACEITES_TECNICOS= "listarAceitesTecnicos";	
	private static final String PAGINA_SOLICITACAO_COMPRA_CRUD = "compras-solicitacaoCompraCRUD";	
	
	private String descricaoNotificacaoTecnica;
	private DominioStatusNotificacaoTecnica dominioStatusNotificacaoTecnica;
	private List<AceiteTecnicoParaSerRealizadoVO> listaAceiteTecnicoCadastroNotificacaoVO;
	private PtmNotificacaoTecnica ptmNotificacaoTecnica;
	private RapServidores rapServidores;
	
	@EJB
    private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	private String paginaRetorno;

	@PostConstruct
	protected void inicializar() {
		begin(conversation);
	}
	
	public String gravarNotificacaoTecnica() throws ApplicationBusinessException{
		String retorno = null;
		if(Strings.isNullOrEmpty(descricaoNotificacaoTecnica)){
			apresentarMsgNegocio(Severity.INFO,"LABEL_DESCRICAO_OBRIGATORIA");			
		}
		if(null == dominioStatusNotificacaoTecnica){
			apresentarMsgNegocio(Severity.INFO,"LABEL_TIPO_OBRIGATORIO");			
		}
		if(!Strings.isNullOrEmpty(descricaoNotificacaoTecnica) && (null != dominioStatusNotificacaoTecnica)){
			for(AceiteTecnicoParaSerRealizadoVO item:listaAceiteTecnicoCadastroNotificacaoVO){
				rapServidores = new RapServidores();
				ptmNotificacaoTecnica = new PtmNotificacaoTecnica();
				String usuarioLogado = obterLoginUsuarioLogado();
				rapServidores = registroColaboradorFacade.obterServidorAtivoPorUsuario(usuarioLogado);
				ptmNotificacaoTecnica.setServidor(rapServidores);
				ptmNotificacaoTecnica.setData(new Date());
				ptmNotificacaoTecnica.setDescricao(descricaoNotificacaoTecnica);
				ptmNotificacaoTecnica.setStatus(dominioStatusNotificacaoTecnica.getCodigo());
				ptmNotificacaoTecnica.setIrpSeq(this.patrimonioFacade.pesquisarIrpSeq(item.getRecebimento(), item.getItemRecebimento(), rapServidores));
				this.patrimonioFacade.gravarNotificacaoTecnica(ptmNotificacaoTecnica);
			}
			apresentarMsgNegocio(Severity.INFO,"NOTIFICACAO_GRAVADA_COM_SUCESSO");
			retorno = PAGINA_LISTA_ACEITES_TECNICOS;	
			this.descricaoNotificacaoTecnica = null;
			this.dominioStatusNotificacaoTecnica = null;
		}		
		return retorno;
	}	
	
	public Integer pesquisarNotaFiscal(Integer numero){
		Integer numeroNotaFiscal = this.patrimonioFacade.pesquisarNumeroNotaFiscal(numero);
		return numeroNotaFiscal;
	}
	
	public String voltar(){
		descricaoNotificacaoTecnica = null;
		dominioStatusNotificacaoTecnica = null;
		if (paginaRetorno == null) {
			return PAGINA_LISTA_ACEITES_TECNICOS;
		} else {
			String retorno = paginaRetorno;
			paginaRetorno = null;
			return retorno;
		}
	}
	
	public String redirecionarSolicitacaoCompra() {
		return PAGINA_SOLICITACAO_COMPRA_CRUD;
	}

	public String getDescricaoNotificacaoTecnica() {
		return descricaoNotificacaoTecnica;
	}

	public void setDescricaoNotificacaoTecnica(String descricaoNotificacaoTecnica) {
		this.descricaoNotificacaoTecnica = descricaoNotificacaoTecnica;
	}

	public DominioStatusNotificacaoTecnica getDominioStatusNotificacaoTecnica() {
		return dominioStatusNotificacaoTecnica;
	}

	public void setDominioStatusNotificacaoTecnica(
			DominioStatusNotificacaoTecnica dominioStatusNotificacaoTecnica) {
		this.dominioStatusNotificacaoTecnica = dominioStatusNotificacaoTecnica;
	}

	public List<AceiteTecnicoParaSerRealizadoVO> getListaAceiteTecnicoCadastroNotificacaoVO() {
		return listaAceiteTecnicoCadastroNotificacaoVO;
	}

	public void setListaAceiteTecnicoCadastroNotificacaoVO(
			List<AceiteTecnicoParaSerRealizadoVO> listaAceiteTecnicoCadastroNotificacaoVO) {
		this.listaAceiteTecnicoCadastroNotificacaoVO = listaAceiteTecnicoCadastroNotificacaoVO;
	}

	public PtmNotificacaoTecnica getPtmNotificacaoTecnica() {
		return ptmNotificacaoTecnica;
	}

	public void setPtmNotificacaoTecnica(PtmNotificacaoTecnica ptmNotificacaoTecnica) {
		this.ptmNotificacaoTecnica = ptmNotificacaoTecnica;
	}

	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	public String getPaginaRetorno() {
		return paginaRetorno;
	}

	public void setPaginaRetorno(String paginaRetorno) {
		this.paginaRetorno = paginaRetorno;
	}
}
