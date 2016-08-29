package br.gov.mec.aghu.ambulatorio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipGrupoFamiliarPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.PacienteGrupoFamiliarVO;
import br.gov.mec.aghu.prescricaomedica.action.VerificarPrescricaoMedicaController.VerificarPrescricaoMedicaControllerExceptionCode;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
/**
 * Controller da estoria 42462 - Prontuário Familia : Agrupamento de Pacientes'.
 */
public class AgrupamentoFamiliarController extends ActionController {
	private static final long serialVersionUID = 4174414765829586654L;
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ProntuariosSugeridosPaginatorController prontuarioSugeridosPaginator;
	@Inject
	private FamiliaresVinculadosPaginatorController familiaresVinculadosPaginator;
	private static final String ERROR_OPERACAO = "ERROR_OPERACAO";
	private static final String MSG_VINCULADO = "MSG_VINCULADO";
	private static final String PACIENTE_PESQUISA_PACIENTE_COMPONENTE = "paciente-pesquisaPacienteComponente";
	
	private AipPacientes pacienteContexto;
	
	private AipPacientes paciente;
	private Integer pacCodigoFonetica;//Parâmetro codigoPaciente
	
	private AipEnderecosPacientes enderecoPacienteContexto;

	private PacienteGrupoFamiliarVO familiarVinculadoSelecionado;
	private PacienteGrupoFamiliarVO prontuarioSugeridoSelecionado;
	private AipGrupoFamiliarPacientes grupoFamiliar = new AipGrupoFamiliarPacientes();
	private String cameFrom;
	private Boolean alteraGrupoFamilia;
	private Integer abaAtiva;
	
	private Integer prontuarioFamiliarInformado;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		
	}
	
	public void desvincularPacienteContexto(){
		if(pacienteContexto.getGrupoFamiliarPaciente().getAgfSeq()== null){
			apresentarMsgNegocio(Severity.WARN, "MSG_ERRO_VINCULO_PACIENTE");
		}else{
			try {
				ambulatorioFacade.desvincularPacienteGrupoFamiliar(this.pacienteContexto.getCodigo());
				iniciar();
				apresentarMsgNegocio(Severity.INFO, "MSG_DESVINCULADO", new Object[] {pacienteContexto.getNome()});
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}catch (Exception e){
				apresentarMsgNegocio(Severity.FATAL, ERROR_OPERACAO, new Object[] {e.getMessage()});
			}
		}
	}
	
	public void desvincularFamiliar(){
			try {
				 ambulatorioFacade.desvincularPacienteGrupoFamiliar(this.familiarVinculadoSelecionado.getCodigo());
			      iniciar();
				 apresentarMsgNegocio(Severity.INFO, "MSG_DESVINCULADO", new Object[] {familiarVinculadoSelecionado.getNome()});
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}catch (Exception e){
				apresentarMsgNegocio(Severity.FATAL, ERROR_OPERACAO, new Object[] {e.getMessage()});
			}
	}
	
	/**
	 * 
	 * 1 - Caso paciente contexto e paciente sugerido da Aba pesquisar prontuario (*Obs: So acontece para este caso ) não estiverem vinculados a nenhum grupo familiar executar (Passo 1)
	 * 2 - Caso paciente contexto não tem grupo familia associar com o selecionado na grid de pacientes sugeridos Executar (Passo 2)
	 * 3 - Caso Caso paciente contexto estiver associado a um grupo familia associar o paciente sugerido ao mesmo grupo familia do contexto executar (Passo 3)
	 */
	public void vincularPacienteSugerido(){
			try {
				if(pacienteContexto.getGrupoFamiliarPaciente().getAgfSeq()== null && prontuarioSugeridoSelecionado.getAgfSeq() == null) {
					//PASSO 1
					AghParametros pInformaProntuarioFamilia = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_INFORMA_PRONTUARIO_FAMILIA);
					if (pInformaProntuarioFamilia != null && "S".equals(pInformaProntuarioFamilia.getVlrTexto())) {
						prontuarioFamiliarInformado = null;
						openDialog("modalInformaProntuarioFamiliaWG");
					}
					else {		
						ambulatorioFacade.vincularAmbosPacienteGrupoFamiliar(pacienteContexto.getCodigo(), this.prontuarioSugeridoSelecionado.getCodigo(), null);
						apresentarMsgNegocio(Severity.INFO, "MSG_VINCULADO_2", new Object[] {pacienteContexto.getNome(),this.prontuarioSugeridoSelecionado.getNome()});
					}
				}else if(pacienteContexto.getGrupoFamiliarPaciente().getAgfSeq()== null){
					//PASSO 2
					ambulatorioFacade.vincularPacienteGrupoFamiliar(this.pacienteContexto.getCodigo(), this.prontuarioSugeridoSelecionado.getAgfSeq());
					apresentarMsgNegocio(Severity.INFO, MSG_VINCULADO, new Object[] {pacienteContexto.getNome()});
				}
				else{
					//PASSO 3
					ambulatorioFacade.vincularPacienteGrupoFamiliar(this.prontuarioSugeridoSelecionado.getCodigo(), pacienteContexto.getGrupoFamiliarPaciente().getAgfSeq());
					apresentarMsgNegocio(Severity.INFO, MSG_VINCULADO, new Object[] {prontuarioSugeridoSelecionado.getNome()});
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}catch (Exception e){
				apresentarMsgNegocio(Severity.FATAL, ERROR_OPERACAO, new Object[] {e.getMessage()});
			}finally{
				iniciar();
			}
	}
	
	public void realizarAgrupamentoFamiliarProntuarioInformado() {
		try {
			ambulatorioFacade.vincularAmbosPacienteGrupoFamiliar(pacienteContexto.getCodigo(), this.prontuarioSugeridoSelecionado.getCodigo(), prontuarioFamiliarInformado);
			apresentarMsgNegocio(Severity.INFO, "MSG_VINCULADO_2", new Object[] {pacienteContexto.getNome(),this.prontuarioSugeridoSelecionado.getNome()});
			iniciar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void vincularPacientePesquisado(){
		if(paciente!= null){
			PacienteGrupoFamiliarVO pacientePesquisaVo = new PacienteGrupoFamiliarVO();
			AipGrupoFamiliarPacientes grupoFamiliarPacientePesquisado = ambulatorioFacade.obterProntuarioFamiliaPaciente(paciente.getCodigo());
			if(grupoFamiliarPacientePesquisado!= null){
				pacientePesquisaVo.setAgfSeq(grupoFamiliarPacientePesquisado.getAgfSeq());
			}
				pacientePesquisaVo.setNome(paciente.getNome());
				pacientePesquisaVo.setCodigo(paciente.getCodigo());
				this.prontuarioSugeridoSelecionado = pacientePesquisaVo;
			if(pacienteContexto.getGrupoFamiliarPaciente().getAgfSeq()!= null && prontuarioSugeridoSelecionado.getAgfSeq()!=null){
				RequestContext.getCurrentInstance().execute("PF('modalConfirmaOperacaoWG').show()");
			}else{
				vincularPacienteSugerido();
			}
		}else{
			apresentarMsgNegocio(Severity.INFO, "MSG_OBRIGATORIO_PESQUISAFONETICA_AGRUPAMENTO_FAMILIAR");
		}
		iniciar();
	}
	/**
	 * Altera grupo familiar do paciente pesquisado em pesquisa fonetica para o do contexto
	 */
	public void alterarGrupoFamiliarPacientePesquisado(){
		try {
			ambulatorioFacade.atualizarPacientePesquisadoGrupoFamiliar(this.prontuarioSugeridoSelecionado.getCodigo(), this.pacienteContexto.getGrupoFamiliarPaciente().getAgfSeq());
			apresentarMsgNegocio(Severity.INFO, MSG_VINCULADO, new Object[] {prontuarioSugeridoSelecionado.getNome()});
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}catch (Exception e){
			apresentarMsgNegocio(Severity.FATAL, ERROR_OPERACAO, new Object[] {e.getMessage()});
		}
	}


	public String formatar(Object item){
		
		StringBuilder str = new StringBuilder("");
		if(item != null){
			String prontuario = item.toString();
			str = new StringBuilder(item.toString());
			str.insert(prontuario.length() - 1, "/");
		}
		return str.toString();
	}
	
	public void iniciar(){
		// Busca as informações do paciente caso já tenha sido feita a pesquisa fonética
		if (pacCodigoFonetica != null) {
			abaAtiva = 1; //Aba da grid prontuario sugerido ou pesquisa 
			// #28133 Carregar as informações do paciente quando quando vir da tela consultaCo ou abaAtendimento (Módulo Emergncia).
				try {
					obterInformacoesPaciente(true);
					pacCodigoFonetica = null;
				} catch (ApplicationBusinessException e) {
					apresentarMsgNegocio(Severity.INFO, e.getMessage());
				}
		}else{
			abaAtiva = 0;
			paciente = null; // apaga campos do pesquisa paciente
		}
		if(pacienteContexto == null){
			Integer codigo = 2237801;
			pacienteContexto = ambulatorioFacade.obterPacienteFull(codigo);
		} 
		if(pacienteContexto!=null){
			grupoFamiliar = ambulatorioFacade.obterProntuarioFamiliaPaciente(pacienteContexto.getCodigo());
		
			if(grupoFamiliar == null){
				grupoFamiliar = new AipGrupoFamiliarPacientes();
			}
			obterEnderecoPadraoPacienteContexto();
			pacienteContexto.setGrupoFamiliarPaciente(grupoFamiliar);
			prontuarioSugeridosPaginator.setPacienteContexto(pacienteContexto);
			prontuarioSugeridosPaginator.setEnderecoPacienteContexto(this.enderecoPacienteContexto);
			familiaresVinculadosPaginator.setPacienteContexto(pacienteContexto);
			//Somente pesquisar por prontuario sugeridos se paciente Contexto tiver endereco
			if(this.enderecoPacienteContexto != null){
			prontuarioSugeridosPaginator.pesquisar();
			}
			familiaresVinculadosPaginator.pesquisar();
		}
		
	}
	

	private void obterEnderecoPadraoPacienteContexto(){
		List<AipEnderecosPacientes> enderecos = pacienteFacade.obterEnderecosPacientes(pacienteContexto.getCodigo());
		if(enderecos != null){
			for (AipEnderecosPacientes e : enderecos){
				if (e.isPadrao()){
					this.enderecoPacienteContexto = e;
				}
			}
		}
	}
	
	/**
	 * Obtem as informações do paciente (nome, codigo e prontuario) e retorna se foi possivel obter
	 * 
	 * @return true caso consiga obter as informações, false caso contrário
	 *  
	 */
	private boolean obterInformacoesPaciente(boolean inicio) throws ApplicationBusinessException {
		if (pacCodigoFonetica == null) {
			this.apresentarMsgNegocio(Severity.ERROR, 
					VerificarPrescricaoMedicaControllerExceptionCode.MENSAGEM_DADOS_MINIMOS_CONSULTA_PACIENTE.toString());
    	} else if (pacCodigoFonetica != null && inicio) {
			paciente = pacienteFacade.obterPacientePorCodigo(pacCodigoFonetica);
		}
		return false;
	}
	public String redirecionarPesquisaFonetica() {
		return PACIENTE_PESQUISA_PACIENTE_COMPONENTE;
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	public void adicionaVirgula(StringBuilder sb){
		if(!sb.toString().equalsIgnoreCase("")){
			sb.append(", ");
		}
	}
	public String formatarEndereco(Object o){
		if(o != null){
			PacienteGrupoFamiliarVO item = (PacienteGrupoFamiliarVO)o;
			StringBuilder sb = new StringBuilder("");
			if(item.getLogradouro() != null){
				sb.append(item.getLogradouro());
			}
			if(item.getNroLogradouro() != null){
				adicionaVirgula(sb);
				sb.append(item.getNroLogradouro().toString());
			}
			if(item.getComplLogradouro() != null){
				adicionaVirgula(sb);
				sb.append(item.getComplLogradouro());
			}
			if(item.getBairro() != null){
				adicionaVirgula(sb);
				sb.append(item.getBairro());
			}
			if(item.getCidade() != null){
				adicionaVirgula(sb);
				sb.append(item.getCidade());
			}
			if(item.getUfSigla() != null){
				adicionaVirgula(sb);
				sb.append(item.getUfSigla());
			}
			return sb.toString();
		}
		return "";
		
	}
	public void limparCampos(){
		this.setPaciente(null);
	}
	
	public String voltar() {
		return cameFrom;
	}
	
	public AipPacientes getPacienteContexto() {
		return pacienteContexto;
	}

	public void setPacienteContexto(AipPacientes pacienteContexto) {
		this.pacienteContexto = pacienteContexto;
	}

	public ProntuariosSugeridosPaginatorController getProntuarioSugeridosPaginator() {
		return prontuarioSugeridosPaginator;
	}

	public void setProntuarioSugeridosPaginator(ProntuariosSugeridosPaginatorController prontuarioSugeridosPaginator) {
		this.prontuarioSugeridosPaginator = prontuarioSugeridosPaginator;
	}

	public FamiliaresVinculadosPaginatorController getFamiliaresVinculadosPaginator() {
		return familiaresVinculadosPaginator;
	}

	public void setFamiliaresVinculadosPaginator(FamiliaresVinculadosPaginatorController familiaresVinculadosPaginator) {
		this.familiaresVinculadosPaginator = familiaresVinculadosPaginator;
	}

	public PacienteGrupoFamiliarVO getFamiliarVinculadoSelecionado() {
		return familiarVinculadoSelecionado;
	}

	public void setFamiliarVinculadoSelecionado(PacienteGrupoFamiliarVO familiarVinculadoSelecionado) {
		this.familiarVinculadoSelecionado = familiarVinculadoSelecionado;
	}

	public PacienteGrupoFamiliarVO getProntuarioSugeridoSelecionado() {
		return prontuarioSugeridoSelecionado;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public void setProntuarioSugeridoSelecionado(PacienteGrupoFamiliarVO prontuarioSugeridoSelecionado) {
		this.prontuarioSugeridoSelecionado = prontuarioSugeridoSelecionado;
	}

	public Boolean getAlteraGrupoFamilia() {
		return alteraGrupoFamilia;
	}

	public void setAlteraGrupoFamilia(Boolean alteraGrupoFamilia) {
		this.alteraGrupoFamilia = alteraGrupoFamilia;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public Integer getAbaAtiva() {
		return abaAtiva;
	}

	public void setAbaAtiva(Integer abaAtiva) {
		this.abaAtiva = abaAtiva;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public Integer getProntuarioFamiliarInformado() {
		return prontuarioFamiliarInformado;
	}

	public void setProntuarioFamiliarInformado(
			Integer prontuarioFamiliarInformado) {
		this.prontuarioFamiliarInformado = prontuarioFamiliarInformado;
	}



}