package br.gov.mec.aghu.ambulatorio.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.MamItemReceituarioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituariosDAO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoReceituario;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MamItemReceituario;
import br.gov.mec.aghu.model.MamItemReceituarioId;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class ManterSumarioAltaReceitasON extends BaseBusiness {
	
	private static final String INCLUIR = "incluir";

	private static final String ALTERAR = "alterar";

	private static final String EXCLUIR = "excluir";

	private static final Log LOG = LogFactory.getLog(ManterSumarioAltaReceitasON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private MamItemReceituarioDAO mamItemReceituarioDAO;
	
	@Inject
	private MamReceituariosDAO mamReceituariosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7670691054940159553L;

	public enum ManterSumarioAltaReceitasExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_RECEITUARIO_SEM_ITEM, MENSAGEM_ITEM_RECEITUARIO_INVALIDADA_CONSTRAINT, MAM_02927, MAM_02252, MAM_00572, MAM_00573, MAM_00574;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public List<MamItemReceituario> buscarItensReceita(
			MpmAltaSumario altaSumario, DominioTipoReceituario tipo) {
		List<MamItemReceituario> lista = new ArrayList<MamItemReceituario>();
		MamReceituarios receituario = this.getMamReceituariosDAO()
				.buscarReceituarioPorAltaSumario(altaSumario, tipo);
		if (receituario != null) {
			return this.getMamItemReceituarioDAO().pesquisarMamItemReceituario(
					receituario.getSeq());
		}
		return lista;
	}
	
	
	public List<MamItemReceituario> buscarItensReceita(MamReceituarios receituario){
			return this.getMamItemReceituarioDAO().pesquisarMamItemReceituario(
					receituario.getSeq());
	}

	/**
	 * Grava inclusão de receituario criando itens associados.
	 * 
	 * @param receituario
	 *            receituario para inclusão
	 * @param novos
	 *            itens para inclusão
	 */
	public void gravar(MamReceituarios receituario,
			List<MamItemReceituario> novos) throws BaseException {

		if (novos == null || novos.isEmpty()) {
			throw new ApplicationBusinessException(
					ManterSumarioAltaReceitasExceptionCode.MENSAGEM_RECEITUARIO_SEM_ITEM);
		}

		// RN04 - Consiste nro de vias
		validaNumeroVias(receituario);

		this.inserir(receituario);
		this.inserir(receituario, novos);
	}

	/**
	 * Grava alterações no Receituarios criando, alterando ou excluido itens
	 * associados.
	 * 
	 * @param receituario
	 *            receituario para alteração
	 * @param novos
	 *            itens para inclusão
	 * @param alterados
	 *            itens para alteração
	 * @param excluidos
	 *            itens para exclusão
	 * @throws CloneNotSupportedException
	 */
	public void gravar(MamReceituarios receituario,
			List<MamItemReceituario> novos, List<MamItemReceituario> alterados,
			List<MamItemReceituario> excluidos) throws BaseException,
			CloneNotSupportedException {

		// RN04 - Consiste nro de vias
		validaNumeroVias(receituario);

		this.alterar(receituario, novos, alterados, excluidos);
	}

	/**
	 * Realizar a inclusão de Receituario
	 * 
	 * @param receituario
	 */
	public void inserir(MamReceituarios receituario)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		// se IndPendente for Nulo, então inclui como P=Pendente
		if (receituario.getPendente() == null) {
			receituario.setPendente(DominioIndPendenteAmbulatorio.P);
		}

		// nao impresso
		receituario.setIndImpresso(DominioSimNao.N);

		// atualiza paciente - se tiver Consulta então busca da Consulta
		// se não então busca da Alta
		if (receituario.getConsulta() != null) {
			receituario.setPaciente(receituario.getConsulta().getPaciente());
		} else {
			receituario.setPaciente(receituario.getMpmAltaSumario()
					.getPaciente());
		}

		// atualiza as informações do servidor e data de criação
		receituario.setServidor(servidorLogado);
		receituario.setDthrCriacao(Calendar.getInstance().getTime());

		this.getMamReceituariosDAO().persistir(receituario);
		this.getMamReceituariosDAO().flush();
	}

	/**
	 * Inserir itens receituarios associados ao receituario fornecido.
	 * 
	 * @param receituario
	 * 
	 * @param itens
	 *            itens de receituarios
	 */
	public void inserir(MamReceituarios receituario,
			List<MamItemReceituario> itens) throws ApplicationBusinessException {

		Short seqp = this.getMamItemReceituarioDAO().buscarProximoSeqp(
				receituario.getSeq());

		for (MamItemReceituario item : itens) {
			// montar chave composta dos itens com a chave da dieta
			MamItemReceituarioId id = new MamItemReceituarioId(
					receituario.getSeq(), ++seqp);
			item.setId(id);
			this.inserir(item);
		}

	}
	
	
	public void inserirItem(MamReceituarios receituario,
			MamItemReceituario item) throws ApplicationBusinessException {

		Short seqp = this.getMamItemReceituarioDAO().buscarProximoSeqp(
				receituario.getSeq());
		if (item.getIndUsoContinuo() == null){
			item.setIndUsoContinuo(DominioSimNao.N);
		}
		MamItemReceituarioId id = new MamItemReceituarioId(
				receituario.getSeq(), ++seqp);
		item.setId(id);
		this.inserirItem(item);
	}	

	
	public void atualizarItem(MamItemReceituario item) throws ApplicationBusinessException {
		this.getMamItemReceituarioDAO().atualizar(item);
		this.getMamItemReceituarioDAO().flush();
	}	
	

	/**
	 * Realizar a inserção dos itens do Receituario
	 * 
	 * @param item
	 */
	public void inserir(MamItemReceituario item) throws ApplicationBusinessException {
		try {

			// campo não manipulado na tela
			item.setIndValidadeProlongada(DominioSimNao.N);

			this.getMamItemReceituarioDAO().persistir(item);
			this.getMamItemReceituarioDAO().flush();

			// busca dieta do persistent context para atualizar
			MamReceituarios receituario = this.obterReceituarioPeloSeq(item
					.getId().getRctSeq());

			receituario.getMamItemReceituario().add(item);

		} catch (BaseRuntimeException e) {
			String erro = "";
			if (e.getCode() != null) {
				erro = e.getMessage();
			}
			throw new ApplicationBusinessException(
					ManterSumarioAltaReceitasExceptionCode.MENSAGEM_ITEM_RECEITUARIO_INVALIDADA_CONSTRAINT,
					e, erro);
		}
	}

	
	public void inserirItem(MamItemReceituario item) throws ApplicationBusinessException {
		item.setIndValidadeProlongada(DominioSimNao.N);
		this.getMamItemReceituarioDAO().persistir(item);
		this.getMamItemReceituarioDAO().flush();
	}	
	
	
	/**
	 * RN02 - Validação da Validade em meses de um item da receita
	 * 
	 * @param item
	 * @throws BaseException
	 */
	public void validaValidadeEmMeses(MamItemReceituario item)
			throws BaseException {

		if (item.getValidadeMeses() != null) {

			IParametroFacade parametroFacade = this.getParametroFacade();

			AghParametros minMesesValido = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_MIN_MESES_VALID_RCT);

			AghParametros maxMesesValido = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_MAX_MESES_VALID_RCT);

			if ((minMesesValido != null) && (maxMesesValido != null)) {
				if (!(item.getValidadeMeses() >= minMesesValido.getVlrNumerico().byteValue())
						|| !(item.getValidadeMeses() <= maxMesesValido.getVlrNumerico().byteValue())) {
					throw new ApplicationBusinessException(
							ManterSumarioAltaReceitasExceptionCode.MAM_02927,
							minMesesValido.getVlrNumerico().byteValue(),
							maxMesesValido.getVlrNumerico().byteValue());
				}
			}
		}
	}

	/**
	 * RN04 - Consiste Número de vias da receita
	 * 
	 * @param receituario
	 * @throws BaseException
	 */
	public void validaNumeroVias(MamReceituarios receituario)
			throws ApplicationBusinessException {

		if ((receituario.getNroVias() == null)
				|| (receituario.getNroVias() == 0)) {
			throw new ApplicationBusinessException(
					ManterSumarioAltaReceitasExceptionCode.MAM_02252);

		}
	}

	/**
	 * RN06 - Verifica Ind_Pendente=V na inclusão
	 * 
	 * @param receituario
	 * @throws BaseException
	 */

	public void validaIndPendenteInclusao(MamReceituarios receituario)
			throws ApplicationBusinessException {
		if (DominioIndPendenteAmbulatorio.V
				.equals(receituario.getPendente())) {
			throw new ApplicationBusinessException(
					ManterSumarioAltaReceitasExceptionCode.MAM_00572);
		}

	}

	/**
	 * RN10 - Verifica Ind_Pendente=V na alteracao Se a receita já está validada
	 * não permite alterar
	 * 
	 * @param receituario
	 * @throws BaseException
	 */

	public void validaIndPendenteAlteracao(MamReceituarios receituario)
			throws ApplicationBusinessException {
		if (DominioIndPendenteAmbulatorio.V
				.equals(receituario.getPendente())) {
			throw new ApplicationBusinessException(
					ManterSumarioAltaReceitasExceptionCode.MAM_00573);
		}

	}

	/**
	 * RN11 - Verifica Ind_Pendente=V na exclusão Se a receita já está validada
	 * não permite excluir
	 * 
	 * @param receituario
	 * @throws BaseException
	 */
	public void validaIndPendenteExclusao(MamReceituarios receituario)
			throws ApplicationBusinessException {
		if (DominioIndPendenteAmbulatorio.V
				.equals(receituario.getPendente())) {
			throw new ApplicationBusinessException(
					ManterSumarioAltaReceitasExceptionCode.MAM_00574);
		}

	}

	/**
	 * Faz uma pre validação do item antes de incluir e alterar na Lista
	 * 
	 * @param MamItemReceituario
	 */
	public void preValidar(MamItemReceituario item, String operacao)
			throws BaseException {
		// RN02 - Consiste Validade (em meses) do item da receita
		validaValidadeEmMeses(item);

		if ((item.getId() != null) && (item.getId().getRctSeq() != null)) {
			MamReceituarios receituario = obterReceituarioPeloSeq(item.getId()
					.getRctSeq());
			preValidar(receituario, operacao);
		}

	}

	/**
	 * Faz uma pre validação do Receituario
	 * 
	 * @param MamItemReceituario
	 * @throws BaseException 
	 */
	public void preValidar(MamReceituarios receituario, String operacao)
			throws ApplicationBusinessException {
		// RN02 - Consiste Validade (em meses) do item da receita

		if (INCLUIR.equals(operacao)) {
			// RN06- Se a receita ja esta validada não permite incluir a receita
			validaIndPendenteInclusao(receituario);
		}

		if (ALTERAR.equals(operacao)) {
			// RN10 - Se a receita ja esta validada não permite alterar a
			// receita
			validaIndPendenteAlteracao(receituario);
		}

		if (EXCLUIR.equals(operacao)) {
			// RN11 - Se a receita ja esta validada não permite excluir a
			// receita
			validaIndPendenteExclusao(receituario);
		}

	}

	/**
	 * atualizar o itens
	 * 
	 * @param receituario
	 * @throws CloneNotSupportedException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 */
	public void alterar(MamReceituarios receituario,
			List<MamItemReceituario> novos, List<MamItemReceituario> alterados,
			List<MamItemReceituario> excluidos) throws BaseException,
			CloneNotSupportedException {

		if (receituario == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		// atualizar itens da receita (inclusão/alteração/exclusao)
		this.atualizarItensReceituario(receituario, novos, alterados, excluidos);

		this.getMamReceituariosDAO().flush();
	}

	/**
	 * Atualizar os itens do Receituario
	 * 
	 * @param receituario
	 * @param novos
	 * @param alterados
	 * @param excluidos
	 */
	public void atualizarItensReceituario(MamReceituarios receituario,
			List<MamItemReceituario> novos, List<MamItemReceituario> alterados,
			List<MamItemReceituario> excluidos) throws BaseException {

		if (excluidos != null && !excluidos.isEmpty()) {

			this.excluir(excluidos);

			// RN12 - Verifica se é o último item do receituário então exclui o
			// receituário
			List<MamItemReceituario> item = buscarItensReceita(
					receituario.getMpmAltaSumario(), receituario.getTipo());
			if (item.isEmpty()) {
				this.excluir(receituario);
			}
		}

		if (novos != null && !novos.isEmpty()) {
			this.inserir(receituario, novos);
		}

		for (MamItemReceituario item : alterados) {
			this.alterar(item);
		}

	}

	/**
	 * Remover itens que foram excluídos na alteração do Receituario
	 * 
	 * @throws BaseException
	 */
	public void excluir(List<MamItemReceituario> itens)
			throws BaseException {

		for (MamItemReceituario itemReceituario : itens) {
			// Faz pré-validações antes de incluir
			this.preValidar(itemReceituario, EXCLUIR);

			this.excluir(itemReceituario);
		}
	}

	/**
	 * Realizar a exclusão de item do Receituario, executando as consistências
	 * necessárias
	 * 
	 * @param item
	 * @throws BaseException
	 */
	public void excluir(MamItemReceituario item) {

		this.getMamItemReceituarioDAO().desatachar(item);

		item = this.getMamItemReceituarioDAO().obterPorChavePrimaria(
				item.getId());

		this.getMamItemReceituarioDAO().remover(item);
		this.getMamItemReceituarioDAO().flush();
		// retorna receituario do persistent context para atualizacao
		MamReceituarios receituario = this.getMamReceituariosDAO()
				.obterPorChavePrimaria(item.getId().getRctSeq());
		receituario.getMamItemReceituario().remove(item);

	}

	/**
	 * Excluir o receituario e seus itens
	 * 
	 * @param receituario
	 * @throws BaseException 
	 */
	public void excluir(MamReceituarios receituario)
			throws ApplicationBusinessException {

			this.preValidar(receituario, EXCLUIR);

		List<MamItemReceituario> ItemReceituario = buscarItensReceita(
				receituario.getMpmAltaSumario(), receituario.getTipo());
		for (MamItemReceituario item : ItemReceituario) {
			this.excluir(item);
		}

		MamReceituarios rec = this.getMamReceituariosDAO().obterPorChavePrimaria(receituario.getSeq());
		
		if (rec != null) {
			this.getMamReceituariosDAO().remover(rec);
			this.getMamReceituariosDAO().flush();
		}
	}
	
	
	public void excluirReceituario(MamReceituarios receituario) throws ApplicationBusinessException {

		this.preValidar(receituario, EXCLUIR);
		List<MamItemReceituario> ItemReceituario = this.getMamItemReceituarioDAO().pesquisarMamItemReceituario(receituario.getSeq());
		for (MamItemReceituario item : ItemReceituario) {
			this.getMamItemReceituarioDAO().remover(item);
			this.getMamItemReceituarioDAO().flush();
		}
		this.getMamReceituariosDAO().remover(receituario);
		this.getMamReceituariosDAO().flush();
	}
	

	/**
	 * Realizar a alteração dos itens do Receituario
	 * 
	 * @param itemReceituario
	 */
	private void alterar(MamItemReceituario item) throws ApplicationBusinessException {
		this.getMamItemReceituarioDAO().merge(item);
	}


	/**
	 * Obtem receituario por chave primária
	 * 
	 * @param atdSeq
	 * @return
	 */
	public MamReceituarios obterReceituarioPeloSeq(Long atdSeq) {
		if (atdSeq == null) {
			return null;
		}
		return getMamReceituariosDAO().obterPorChavePrimaria(atdSeq);
	}

	/**
	 * Remove Receituario
	 * 
	 * @param altaSumario
	 * @throws BaseException
	 */
	public void removerReceituario(MpmAltaSumario altaSumario)
			throws ApplicationBusinessException {
		if (altaSumario != null) {
			//Busca todos os receituarios tanto geral como especial 
			List<MamReceituarios>  listReceituario = this.getMamReceituariosDAO()
					.obterMamReceituarioList(altaSumario.getId().getApaAtdSeq(),
							altaSumario.getId().getApaSeq(),
							altaSumario.getId().getSeqp(), null);
			
			if(listReceituario != null && listReceituario.size() > 0){
				for (MamReceituarios mamReceituarios : listReceituario) {
					try {
						this.excluir(mamReceituarios);
					} catch (BaseException e) {
						logError(e.getMessage(),e);
					}
				}
			}
		}
	}

	/**
	 * Retorna MpmMamReceituarios
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public MamReceituarios obterMamReceituario(Integer altanAtdSeq,
			Integer altanApaSeq, Short altanAsuSeqp, DominioTipoReceituario tipo) {
		return this.getMamReceituariosDAO().obterMamReceituario(altanAtdSeq,
				altanApaSeq, altanAsuSeqp, tipo);
	}

	/**
	 * Atualiza alta receituario do sumário ativo montando um novo receituário e seus ítens
	 * @throws ApplicationBusinessException
	 */
	public void versionarAltaReceituario(MpmAltaSumario altaSumarioNovo, Short antigoAsuSeqp) throws ApplicationBusinessException {

		List<MamReceituarios> listReceituarios = this.getMamReceituariosDAO().obterReceituarioVersionamento(altaSumarioNovo.getId().getApaAtdSeq(), altaSumarioNovo.getId().getApaSeq(), antigoAsuSeqp);
		
		for (MamReceituarios receituarioOrig : listReceituarios) {
			
			MamReceituarios novoReceituario = new MamReceituarios();
			
		    ///// Sets do novoReceituario
			novoReceituario.setMpmAltaSumario(altaSumarioNovo);
			
			novoReceituario.setTipo(receituarioOrig.getTipo());
			novoReceituario.setNroVias(receituarioOrig.getNroVias());
			novoReceituario.setPendente(DominioIndPendenteAmbulatorio.P);
			novoReceituario.setIndImpresso(receituarioOrig.getIndImpresso());
			novoReceituario.setConsulta(receituarioOrig.getConsulta());
			novoReceituario.setPaciente(receituarioOrig.getPaciente());
			novoReceituario.setServidor(receituarioOrig.getServidor());
			novoReceituario.setDthrCriacao(Calendar.getInstance().getTime());
			novoReceituario.setDthrMvto(receituarioOrig.getDthrMvto());
			novoReceituario.setDthrValida(receituarioOrig.getDthrValida());
			novoReceituario.setDthrValidaMvto(receituarioOrig.getDthrValidaMvto());
			novoReceituario.setReceituario(receituarioOrig.getReceituario());
			novoReceituario.setMamTriagens(receituarioOrig.getMamTriagens());
			novoReceituario.setObservacao(receituarioOrig.getObservacao());
			novoReceituario.setRegistro(receituarioOrig.getRegistro());
			novoReceituario.setServidorMovimento(receituarioOrig.getServidorMovimento());
			novoReceituario.setServidorValida(receituarioOrig.getServidorValida());
			novoReceituario.setServidorValidaMovimento(receituarioOrig.getServidorMovimento());
			
			this.getMamReceituariosDAO().persistir(novoReceituario);
			this.getMamReceituariosDAO().flush();
			
			
			//Buscar lista de Itens de Receita Original
			Set<MamItemReceituario> itensReceituario = receituarioOrig.getMamItemReceituario();
			
			for(MamItemReceituario itemReceitaOrig : itensReceituario){
				
				MamItemReceituario novoItemReceita = new MamItemReceituario();
				
				novoItemReceita.setReceituario(novoReceituario);
				
				novoItemReceita.setId(new MamItemReceituarioId(novoReceituario.getSeq(), itemReceitaOrig.getId().getSeqp()));
				
				novoItemReceita.setDescricao(itemReceitaOrig.getDescricao());
				novoItemReceita.setFormaUso(itemReceitaOrig.getFormaUso());
				novoItemReceita.setIndInterno(itemReceitaOrig.getIndInterno());
				novoItemReceita.setIndSituacao(itemReceitaOrig.getIndSituacao());
				novoItemReceita.setIndUsoContinuo(itemReceitaOrig.getIndUsoContinuo());
				novoItemReceita.setIndValidadeProlongada(itemReceitaOrig.getIndValidadeProlongada());
				novoItemReceita.setNroGrupoImpressao(itemReceitaOrig.getNroGrupoImpressao());
				novoItemReceita.setOrdem(itemReceitaOrig.getOrdem());
				novoItemReceita.setQuantidade(itemReceitaOrig.getQuantidade());
				novoItemReceita.setTipoPrescricao(itemReceitaOrig.getTipoPrescricao());
				novoItemReceita.setValidadeMeses(itemReceitaOrig.getValidadeMeses());
				
				this.getMamItemReceituarioDAO().persistir(novoItemReceita);
				this.getMamItemReceituarioDAO().flush();
			}
		}
		
	}
	
	public List<MamItemReceituario> obterItensReceitaOrdenadoPorSeqp(MamReceituarios receituario){
		return this.getMamItemReceituarioDAO().obterMamItemReceituarioOrdenadoSeqp(receituario.getSeq());
	}

	private MamReceituariosDAO getMamReceituariosDAO() {
		return mamReceituariosDAO;
	}

	private MamItemReceituarioDAO getMamItemReceituarioDAO() {
		return mamItemReceituarioDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
