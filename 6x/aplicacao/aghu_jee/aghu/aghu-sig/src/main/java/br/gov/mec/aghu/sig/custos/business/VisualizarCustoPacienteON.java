package br.gov.mec.aghu.sig.custos.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoProcessamentoCusto;
import br.gov.mec.aghu.dominio.DominioVisaoCustoPaciente;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdPaciente2DAO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdPacienteDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdReceitaDAO;
import br.gov.mec.aghu.sig.dao.SigProcessamentoCustoDAO;
import br.gov.mec.aghu.vo.AghAtendimentosVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class VisualizarCustoPacienteON  extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(VisualizarCustoPacienteON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * Enum de exceções
	 * 
	 * @author thiago
	 */
	public enum VisualizarCustoPacienteONExceptionCode implements
			BusinessExceptionCode{
		PACIENTE_CUSTO_MENSAGEM_CID_DUPLICADO,
		PACIENTE_CUSTO_MENSAGEM_CENTRO_CUSTO_DUPLICADO,
		PACIENTE_CUSTO_MENSAGEM_ESPECIALIDADE_MEDICA_DUPLICADA,
		PACIENTE_CUSTO_MENSAGEM_EQUIPE_MEDICA_DUPLICADA,
		MENSAGEM_ERRO_PESQUISA_PACIENTE,
		MENSAGEM_ALERTA_SELECIONA_PACIENTE,
		MSG_PACIENTE_NAO_ENCONTRADO,
		MSG_PACIENTE_NAO_ENCONTRADO_PARA,
		MENSAGEM_ERRO_PACIENTE_SEM_PRONTUARIO;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 471562651400385874L;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@Inject
	private SigProcessamentoCustoDAO sigProcessamentoCustoDAO;
	
	@Inject
	private SigCalculoAtdPacienteDAO sigCalculoAtdPacienteDAO;
	
	@Inject
	private SigCalculoAtdReceitaDAO sigCalculoAtdReceitaDAO;
	
	@Inject
	private SigCalculoAtdPaciente2DAO sigCalculoAtdPaciente2DAO;
	
	/**
	 * Muda o tipo de visao informado na tela.
	 * 
	 * @param visao
	 */
	public void mudarValorViao(DominioVisaoCustoPaciente visao){
		if(visao == DominioVisaoCustoPaciente.PACIENTE){
			visao = DominioVisaoCustoPaciente.COMPETENCIA;
		} else {
			visao = DominioVisaoCustoPaciente.PACIENTE;
		}
	}
	
	/**
	 * Retorna a lista de SigProcessamentoCusto se o tipo de visao for COMPETENCIA. Do contrario é retornado nulo.
	 */
	public List<SigProcessamentoCusto> visaoCompetencia(DominioVisaoCustoPaciente visao){
		if(visao == DominioVisaoCustoPaciente.COMPETENCIA){
			return this.obterListaCompetencias();
		}
		
		return null;
	}
	
	private List<SigProcessamentoCusto> obterListaCompetencias() {
		List<SigProcessamentoCusto> listaProcCusto = this.getSigProcessamentoCustoDAO()
		.obterSigProcessamentoCustoPorSituacao(new DominioSituacaoProcessamentoCusto[] { DominioSituacaoProcessamentoCusto.P, DominioSituacaoProcessamentoCusto.F}, 
				SigProcessamentoCusto.Fields.COMPETENCIA.toString());

		return listaProcCusto;
	}
	


	/**
	 * Adiciona um cid na lista
	 * 
	 * @param listaCID
	 * @param cid
	 * @throws ApplicationBusinessException
	 */
	public void adicionarCIDNaLista(List<AghCid> listaCID, AghCid cid) throws ApplicationBusinessException{
		
		if(cid == null){
			return;
		}
		
		boolean cidDuplicado = false;
		
		if(listaCID == null){
			listaCID = new ArrayList<AghCid>();
		}
		
		for (AghCid cidInt : listaCID) {
			if(cidInt.getSeq().equals(cid.getSeq())){
				cidDuplicado = true;
				break;
			}
		}
	
		if(!cidDuplicado){
			listaCID.add(cid);
		}  else {
			throw new ApplicationBusinessException(VisualizarCustoPacienteONExceptionCode.PACIENTE_CUSTO_MENSAGEM_CID_DUPLICADO);
		}
	}

	/**
	 * Deleta o cid da lista
	 * 
	 * @param listaCID
	 * @param cid
	 */
	public void deletarCIDDaLista(List<AghCid> listaCID, AghCid cid) {
		if(listaCID != null){
			for(int i = 0; i < listaCID.size(); i++){
				if(listaCID.get(i).getSeq().equals(cid.getSeq())){
					listaCID.remove(i);
					return;
				}
			}
		}
	}
	
	/**
	 * Adiciona um centro de custo na lista
	 * 
	 * @param listaCID
	 * @param cid
	 * @throws ApplicationBusinessException
	 */
	public void adicionarCentroCustoNaLista(List<FccCentroCustos> listaCentroCusto, FccCentroCustos centroCusto) throws ApplicationBusinessException{
		
		if(centroCusto == null){
			return;
		} else {
			centroCusto = this.getCentroCustoFacade().obterCentroCustoPorChavePrimaria(centroCusto.getCodigo());
		}
		
		boolean duplicado = false;
		
		if(listaCentroCusto == null){
			listaCentroCusto = new ArrayList<FccCentroCustos>();
		}
		
		for (FccCentroCustos ccusto : listaCentroCusto) {
			if(ccusto.getCodigo().equals(centroCusto.getCodigo())){
				duplicado = true;
				break;
			}
		}

		if(!duplicado){
			listaCentroCusto.add(centroCusto);
		} else {
			throw new ApplicationBusinessException(VisualizarCustoPacienteONExceptionCode.PACIENTE_CUSTO_MENSAGEM_CENTRO_CUSTO_DUPLICADO);
		}
		
	}
	
	/**
	 * Deleta o centro de custo da lista
	 * 
	 * @param listaCID
	 * @param cid
	 */
	public void deletarCentroCustoDaLista(List<FccCentroCustos> listaCentroCusto, FccCentroCustos centroCusto) {
		if(listaCentroCusto != null){
			for(int i = 0; i < listaCentroCusto.size(); i++){
				if(listaCentroCusto.get(i).getCodigo().equals(centroCusto.getCodigo())){
					listaCentroCusto.remove(i);
					return;
				}
			}
		}
	}
	
	/**
	 * Adiciona uam especialidade médica na lista.
	 * 
	 * @param listaCID
	 * @param cid
	 * @throws ApplicationBusinessException
	 */
	public void adicionarEspecialidadesNaLista(List<AghEspecialidades> listaEspecialidades, AghEspecialidades especialidade) throws ApplicationBusinessException{
		
		if(especialidade == null){
			return;
		}
		
		boolean duplicado = false;
		
		if(listaEspecialidades == null){
			listaEspecialidades = new ArrayList<AghEspecialidades>();
		}
		
		for (AghEspecialidades esp : listaEspecialidades) {
			if(esp.getSeq().equals(especialidade.getSeq())){
				duplicado = true;
				break;
			}
		}

		if(!duplicado){
			listaEspecialidades.add(especialidade);
		} else {
			throw new ApplicationBusinessException(VisualizarCustoPacienteONExceptionCode.PACIENTE_CUSTO_MENSAGEM_ESPECIALIDADE_MEDICA_DUPLICADA);
		}
		
	}
	
	/**
	 * Deleta a especialidade médica da lista
	 * 
	 * @param listaCID
	 * @param cid
	 */
	public void deletarEspecialidadesDaLista(List<AghEspecialidades> listaEspecialidades, AghEspecialidades especialidade) {
		if(listaEspecialidades != null){
			for(int i = 0; i < listaEspecialidades.size(); i++){
				if(listaEspecialidades.get(i).getSeq().equals(especialidade.getSeq())){
					listaEspecialidades.remove(i);
					return;
				}
			}
		}
	}
	
	/**
	 * Adiciona uma equipe médica na lista.
	 * 
	 * @param listaEquipes
	 * @param equipes
	 * @throws ApplicationBusinessException
	 */
	public void adicionarEquipesNaLista(List<AghEquipes> listaEquipes, AghEquipes equipes) throws ApplicationBusinessException{
		
		if(equipes == null){
			return;
		}
		
		boolean duplicado = false;
		
		if(listaEquipes == null){
			listaEquipes = new ArrayList<AghEquipes>();
		}
		
		for (AghEquipes eqp : listaEquipes) {
			if(eqp.getSeq().equals(equipes.getSeq())){
				duplicado = true;
				break;
			}
		}

		if(!duplicado){
			listaEquipes.add(equipes);
		} else {
			throw new ApplicationBusinessException(VisualizarCustoPacienteONExceptionCode.PACIENTE_CUSTO_MENSAGEM_EQUIPE_MEDICA_DUPLICADA);
		}
		
	}
	
	/**
	 * Deleta a equipe médica da lista
	 * 
	 * @param listaEspecialidades
	 * @param equipes
	 */
	public void deletarEquipesDaLista(List<AghEquipes> listaEquipes, AghEquipes equipes) {
		if(listaEquipes != null){
			for(int i = 0; i < listaEquipes.size(); i++){
				if(listaEquipes.get(i).getSeq().equals(equipes.getSeq())){
					listaEquipes.remove(i);
					return;
				}
			}
		}
	}
	
	/**
	 * Obtem os profissionais responsaveis de uma lista de AghEquipes
	 * 
	 * @param equipes
	 * @return
	 */
	public List<RapServidores> obterListaReponsaveisPorListaDeEquipes(List<AghEquipes> equipes){
		List<RapServidores> responsaveis = new ArrayList<RapServidores>();
		if(equipes != null){
			for (AghEquipes aghEquipes : equipes) {
				responsaveis.add(aghEquipes.getProfissionalResponsavel());
			}
		}
		return responsaveis;
	}

	/**
	 * Valida informações do paciente. Se paciente invalido, lança exceção.
	 * 
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	public void validarPacienteInformadoNoFiltro(AipPacientes paciente) throws ApplicationBusinessException{
		if(paciente == null || paciente.getCodigo() == null){
			throw new ApplicationBusinessException(VisualizarCustoPacienteONExceptionCode.MENSAGEM_ERRO_PESQUISA_PACIENTE);
		}
		
	}

	/**
	 * Valida se algum item na listagem principal foi selecionado
	 * 
	 * @param listagem
	 * @throws ApplicationBusinessException
	 */
	public void validarConfirmar(List<AghAtendimentosVO> listagem) throws ApplicationBusinessException {
		if(listagem.isEmpty()){
			throw new ApplicationBusinessException(VisualizarCustoPacienteONExceptionCode.MENSAGEM_ALERTA_SELECIONA_PACIENTE);
		}
	}
	
	public List<AghAtendimentosVO> obterSelecionados(List<AghAtendimentosVO> listagem) throws ApplicationBusinessException{
		List<AghAtendimentosVO> selecionados = new ArrayList<AghAtendimentosVO>();
		for (AghAtendimentosVO aghAtendimentosVO : listagem) {
			if(aghAtendimentosVO.getProntuario() == null) {
				throw new ApplicationBusinessException(VisualizarCustoPacienteONExceptionCode.MENSAGEM_ERRO_PACIENTE_SEM_PRONTUARIO);
			}
			if(aghAtendimentosVO.getControleAtd()){
				selecionados.add(aghAtendimentosVO);
			}
		}
		return selecionados;
	}

	public void validarListaVaziaExibeMensagem(List<AghAtendimentosVO> listagem, String[] parametros) throws ApplicationBusinessException {
		if(listagem == null || listagem.size() == 0) {
			String encontradoPara = validaParametros(parametros);
			if(encontradoPara != null) {
				throw new ApplicationBusinessException(VisualizarCustoPacienteONExceptionCode.MSG_PACIENTE_NAO_ENCONTRADO_PARA, encontradoPara);
			} else {
				throw new ApplicationBusinessException(VisualizarCustoPacienteONExceptionCode.MSG_PACIENTE_NAO_ENCONTRADO);
			}
		}
	}
	
	private String validaParametros(String[] parametros) {
		
		String virgula = ", ";
		StringBuffer message = new StringBuffer(70);

		if(!parametros[0].isEmpty()) {
			message.append("a competência " + parametros[0] + virgula);
		}
		
		if(!parametros[1].isEmpty()) {
			message.append("Cid " + parametros[1] + virgula);
		}
		
		if(!parametros[2].isEmpty()) {
			message.append("Centro de Custo " + parametros[2] + virgula);
		}
		
		if(!parametros[3].isEmpty()) {
			message.append("Especialidade Médica " + parametros[3] + virgula);
		}
		
		if(!parametros[4].isEmpty()) {
			message.append("Equipe Médica " + parametros[4] + virgula);
		}
		
		return message.length() > 0 ? message.toString().substring(0, message.length() -2) : null;
	}

	/**
	 * Monta as informações para mostrar na mensagem internacionalizada.
	 * @param competencia
	 * @param listaCID
	 * @param listaCentroCusto
	 * @param listaEquipes
	 * @param listaEspecialidades
	 * @return
	 */
	public String[] buscarInformacoesParaMostrarMensagem(SigProcessamentoCusto competencia, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEquipes> listaEquipes, List<AghEspecialidades> listaEspecialidades) {
		return new String[]{
				this.buscarInformacoesCompetenciaParaMostrarMensagem(competencia),
				this.buscarInformacoesCIDParaMostrarMensagem(listaCID),
				this.buscarInformacoesCentroCustoParaMostrarMensagem(listaCentroCusto),
				this.buscarInformacoesEspecialidadesParaMostrarMensagem(listaEspecialidades),
				this.buscarInformacoesEquipesParaMostrarMensagem(listaEquipes)
		};
	}

	/**
	 * Monta as informações de especialidades para mostrar na mensagem internacionalizada.
	 * @param listaEspecialidades
	 * @return
	 */
	private String buscarInformacoesEspecialidadesParaMostrarMensagem(List<AghEspecialidades> listaEspecialidades) {
		StringBuffer retorno = new StringBuffer("");
		if(listaEspecialidades != null && listaEspecialidades.size() > 0){
			for (int i = 0; i < listaEspecialidades.size(); i++) {
				retorno.append(listaEspecialidades.get(i).getNomeEspecialidade());
				if(i != (listaEspecialidades.size() - 1)){
					retorno.append(", ");
				}
			}
		}
		return retorno.toString();
	}
	
	/**
	 * Monta as informações de equipes para mostrar na mensagem internacionalizada.
	 * 
	 * @param listaEquipes
	 * @return
	 */
	private String buscarInformacoesEquipesParaMostrarMensagem(List<AghEquipes> listaEquipes) {
		StringBuffer retorno = new StringBuffer("");
		if(listaEquipes != null && listaEquipes.size() > 0){
			for (int i = 0; i < listaEquipes.size(); i++) {
				retorno.append(listaEquipes.get(i).getNome());
				if(i != (listaEquipes.size() - 1)){
					retorno.append(", ");
				}
			}
		}
		return retorno.toString();
	}

	/**
	 * Monta as informações de centros de custos para mostrar na mensagem internacionalizada.
	 * 
	 * @param listaCentroCusto
	 * @return
	 */
	private String buscarInformacoesCentroCustoParaMostrarMensagem(List<FccCentroCustos> listaCentroCusto) {
		StringBuffer retorno = new StringBuffer("");
		if(listaCentroCusto != null && listaCentroCusto.size() > 0){
			for (int i = 0; i < listaCentroCusto.size(); i++) {
				retorno.append(listaCentroCusto.get(i).getCodigo());
				if(i != (listaCentroCusto.size() - 1)){
					retorno.append(", ");
				}
			}
		}
		return retorno.toString();
	}

	/**
	 * Monta as informações de CID para mostrar na mensagem internacionalizada.
	 * 
	 * @param listaCID
	 * @return
	 */
	private String buscarInformacoesCIDParaMostrarMensagem(List<AghCid> listaCID) {
		StringBuffer retorno = new StringBuffer("");
		if(listaCID != null && listaCID.size() > 0){
			for (int i = 0; i < listaCID.size(); i++) {
				retorno.append(listaCID.get(i).getCodigo());
				if(i != (listaCID.size() - 1)){
					retorno.append(", ");
				}
			}
		}
		return retorno.toString();
	}

	/**
	 * Monta as informações de competencia para mostrar na mensagem internacionalizada.
	 * 
	 * @param listaCID
	 * @return
	 */
	private String buscarInformacoesCompetenciaParaMostrarMensagem(SigProcessamentoCusto competencia) {
		if(competencia!= null && competencia.getSeq() != null){
			return competencia.getCompetenciaMesAno();
		}

		return "";
	}
	
	/**
	 * Obtém e atualiza na listaAtendimentoVO os valores dos atributos 
	 * valorTotalCusto e valorTotalReceita. Os valores são obtidos 
	 * conforme o prontuario.
	 * 
	 * @param listaAtendimentoVO
	 */
	public void obterValoresCustosReceitasPorProntuario(List<AghAtendimentosVO> listaAtendimentoVO) {
		
		if (!listaAtendimentoVO.isEmpty()) {
			Integer prontuario = listaAtendimentoVO.get(0).getProntuario();
			
			for (AghAtendimentosVO aghAtendimentoVO : listaAtendimentoVO) {
				aghAtendimentoVO.setValorTotalCusto(sigCalculoAtdPacienteDAO.buscarCustoTotal(prontuario, null, false, false, null, null));
				aghAtendimentoVO.setValorTotalReceita(sigCalculoAtdReceitaDAO.obterValorTotalReceita(prontuario, null, null));
				aghAtendimentoVO.setValorUltimaFatura(sigCalculoAtdPaciente2DAO.buscarValorUltimaFatura(prontuario));
			}
		}
	}
	
	/**
	 * Obtém e atualiza na listaAtendimentoVO os valores dos atributos 
	 * valorTotalCusto e valorTotalReceita. Os valores são obtidos 
	 * conforme o prontuario e pmuSeq (seq do processamento).
	 * 
	 * @param listaAtendimentoVO
	 * @param pmuSeq
	 */	
	public void obterValoresCustosReceitasPorProntuarioEProcessamento(List<AghAtendimentosVO> listaAtendimentoVO, Integer pmuSeq) {
		
		if (!listaAtendimentoVO.isEmpty()) {
			for (AghAtendimentosVO aghAtendimentoVO : listaAtendimentoVO) {
				Integer prontuario = aghAtendimentoVO.getProntuario();
				aghAtendimentoVO.setValorTotalCusto(sigCalculoAtdPacienteDAO.buscarCustoTotal(prontuario, pmuSeq, false, false, null, null));
				aghAtendimentoVO.setValorTotalReceita(sigCalculoAtdReceitaDAO.obterValorTotalReceita(prontuario, pmuSeq, null));
				aghAtendimentoVO.setValorUltimaFatura(sigCalculoAtdPaciente2DAO.buscarValorUltimaFatura(prontuario));
			}
		}
	}
	
	protected SigProcessamentoCustoDAO getSigProcessamentoCustoDAO(){
		return sigProcessamentoCustoDAO;
	}
	
	
	protected ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}

}
