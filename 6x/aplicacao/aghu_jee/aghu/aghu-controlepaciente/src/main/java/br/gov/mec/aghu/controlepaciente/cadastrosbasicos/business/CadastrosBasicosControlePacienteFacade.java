package br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.controlepaciente.dao.EcpGrupoControleDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpItemControleDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpItemCtrlCuidadoEnfDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpItemCtrlCuidadoMedicoDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpLimiteItemControleDAO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoGrupoControle;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaIdade;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EcpGrupoControle;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.EcpLimiteItemControle;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.MpmCuidadoUsual;

/**
 * Porta de entrada do sub módulo de cadastros básicos de controle do paciente.
 * 
 * @author agerling
 * 
 */


@Modulo(ModuloEnum.CONTROLE_PACIENTE)
@Stateless
public class CadastrosBasicosControlePacienteFacade extends BaseFacade implements ICadastrosBasicosControlePacienteFacade{

@EJB
private ManterItensControleON manterItensControleON;
@EJB
private AssociarItensPrescricaoON associarItensPrescricaoON;
@EJB
private ManterLimiteItemControleON manterLimiteItemControleON;
@EJB
private ManterGrupoControleON manterGrupoControleON;

@Inject
private EcpItemCtrlCuidadoMedicoDAO ecpItemCtrlCuidadoMedicoDAO;

@Inject
private EcpItemControleDAO ecpItemControleDAO;

@Inject
private EcpLimiteItemControleDAO ecpLimiteItemControleDAO;

@Inject
private EcpGrupoControleDAO ecpGrupoControleDAO;

@Inject
private EcpItemCtrlCuidadoEnfDAO ecpItemCtrlCuidadoEnfDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 9052299347129008111L;

	@Override
	public Long pesquisarItensCount(String sigla, String descricao,
			EcpGrupoControle grupo, DominioSituacao situacao) {
		return this.getManterItensControleON().pesquisarItensCount(sigla,
				descricao, grupo, situacao);
	}

	@Override
	public List<EcpItemControle> pesquisarItens(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, String sigla,
			String descricao, EcpGrupoControle grupo, DominioSituacao situacao) {
		return this.getManterItensControleON().pesquisarItens(firstResult,
				maxResult, orderProperty, asc, sigla, descricao, grupo,
				situacao);
	}

	@Override
	public List<EcpItemControle> listarItensControleAtivos(EcpGrupoControle grupo) {
		return this.getEcpItemControleDAO().listarAtivos(grupo);
	}

	@Override
	public List<EcpGrupoControle> listarGruposControleAtivos() {
		return this.getEcpGrupoControleDAO().listarGruposAtivos();
	}

	/**
	 * Inseri um novo item de controle
	 * @param ecpItemControle
	 * @throws BaseException
	 */
	@Override
	@Secure("#{s:hasPermission('manterItemControle','inserir')}")
	public void inserir(EcpItemControle ecpItemControle)
			throws BaseException {
		this.getManterItensControleON().inserir(ecpItemControle);
	}

	/**
	 * Altera um item de controle
	 * @param itemControle
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('manterItemControle','alterar')}")
	public void alterar(EcpItemControle itemControle)
			throws ApplicationBusinessException {
		this.getManterItensControleON().alterar(itemControle);
	}

	@Override
	public List<EcpGrupoControle> pesquisarGruposControle(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, Integer seq,
			String descricao, Short ordem, DominioSituacao situacao, DominioTipoGrupoControle tipo) {
		
		return this.getManterGrupoControleON().pesquisarGruposControle(firstResult,
				maxResult, orderProperty, asc, seq, descricao, ordem,
				situacao, tipo);
	}
	
	/**
	 * Insere um novo registro de grupo de controle
	 * 
	 * @param _grupoControle
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	@Override
	@Secure("#{s:hasPermission('manterGrupoControle','inserir')}")
	public void inserirGrupoControle(EcpGrupoControle _grupoControle)
			throws ApplicationBusinessException {
		this.getManterGrupoControleON().inserir(_grupoControle);
	}

	/**
	 * Altera um registro de grupo de controle
	 * 
	 * @param _grupoControle
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('manterGrupoControle','alterar')}")
	public void alterarGrupoControle(EcpGrupoControle _grupoControle)
			throws ApplicationBusinessException {
		this.getManterGrupoControleON().atualizar(_grupoControle);
	}

	/**
	 * Excluir registro de grupo de controle
	 */
	@Override
	public void excluirGrupoControle(Integer seq) throws ApplicationBusinessException {
		this.getManterGrupoControleON().excluir(seq);
	}

	/**
	 * Busca a listagem de grupos de controle cadastrados.
	 * 
	 * @param _grupoControleSeq
	 * @param _descricao
	 * @return Lista contendo os grupos cadastrados
	 */
	@Override
	public List<EcpGrupoControle> listarGruposControle(Integer _grupoControleSeq, String _descricao, Short _ordem, DominioSituacao _situacao, DominioTipoGrupoControle tipo) {
		return this.getEcpGrupoControleDAO().listarGruposControle(_grupoControleSeq, _descricao, _ordem, _situacao , tipo);
	}
	
	@Override
	public Long listarGruposControleCount(Integer _seq, String _descricao, Short _ordem, DominioSituacao _situacao , DominioTipoGrupoControle tipo) {
		return this.getEcpGrupoControleDAO().listarGruposControleCount(_seq, _descricao, _ordem, _situacao ,  tipo);
	}

	@Override
	public List<EcpItemControle> verificaReferenciaItemControle (EcpGrupoControle _grupo) {
		return this.getManterGrupoControleON().verificaReferenciaItemControle(_grupo);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterItemControle','excluir')}")
	public void excluir(Short seq) throws ApplicationBusinessException {
		this.getManterItensControleON().excluir(seq);
	}
	
	@Override
	public List<EcpLimiteItemControle> listarLimitesItemControle(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, EcpItemControle itemControle) {

		return this.getEcpLimiteItemControleDAO().pesquisarLimitesItemControle(
				firstResult, maxResult, orderProperty, asc, itemControle);
	}

	@Override
	public Long listarLimitesItemControleCount(EcpItemControle itemControle) {
		return this.getEcpLimiteItemControleDAO()
				.pesquisarLimitesItemControleCount(itemControle);
	}

	@Override
	public EcpLimiteItemControle obterLimiteItemControle(
			Integer seqLimiteItemControle) {
		return this.getEcpLimiteItemControleDAO().obterPorChavePrimaria(
				seqLimiteItemControle);
	}

	@Override
	@Secure("#{s:hasPermission('manterLimiteItemControle','incluir')}")
	public void inserirLimiteItemControle(
			EcpLimiteItemControle ecpLimiteItemControle)
			throws ApplicationBusinessException {
		this.getManterLimiteItemControleON().inserirLimiteItemControle(
				ecpLimiteItemControle);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterLimiteItemControle','alterar')}")
	public void alterarLimiteItemControle(EcpLimiteItemControle ecpLimiteItemControle) throws ApplicationBusinessException {
		this.getManterLimiteItemControleON().alterarLimiteItemControle(ecpLimiteItemControle);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterLimiteItemControle','excluir')}")
	public void excluirLimiteItemControle(Integer seq) throws ApplicationBusinessException {
		this.getManterLimiteItemControleON().excluir(seq);
	}

	// criar Métodos para retornar DAOSs e ONs
	protected EcpItemControleDAO getEcpItemControleDAO() {
		return ecpItemControleDAO;
	}

	protected EcpGrupoControleDAO getEcpGrupoControleDAO() {
		return ecpGrupoControleDAO;
	}
	
	protected EcpLimiteItemControleDAO getEcpLimiteItemControleDAO(){
		return ecpLimiteItemControleDAO;
	}

	protected ManterItensControleON getManterItensControleON() {
		return manterItensControleON;
	}

	protected ManterGrupoControleON getManterGrupoControleON() {
		return manterGrupoControleON;
	}
	
	protected ManterLimiteItemControleON getManterLimiteItemControleON(){
		return manterLimiteItemControleON;
	}

	@Override
	public List<EpeCuidados> obterItensCuidadoEnfermagem(
			EcpItemControle itemControle) {
		return this.getItemCtrlCuidadoEnfDAO().obterItensCuidadoEnfermagem(
				itemControle);
	}

	@Override
	public List<MpmCuidadoUsual> obterItensCuidadoMedico(EcpItemControle itemControle) {
		return this.getItemCtrlCuidadoMedicoDAO().obterItensCuidadoMedico(itemControle);
	}

	@Override
	public EcpItemControle obterItemControle(Short seqItemControle, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin){
		return this.getAssociarItensPrescricaoON().obterItemControle(seqItemControle, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	@Override
	@Secure("#{s:hasPermission('associarItemPrescricao','gravar')}")
	public void salvarAssociacaoCuidadosEnfermagem(
			EcpItemControle itemControle,
			List<EpeCuidados> listaCuidadosIncluir,
			List<EpeCuidados> listaCuidadosExcluir) throws BaseException {
		this.getAssociarItensPrescricaoON().salvarAssociacaoCuidadosEnfermagem(
				itemControle, listaCuidadosIncluir, listaCuidadosExcluir);
	}

	@Override
	@Secure("#{s:hasPermission('associarItemPrescricao','gravar')}")
	public void salvarAssociacaoCuidadosMedicos(EcpItemControle itemControle,
			List<MpmCuidadoUsual> listaCuidadosIncluir,
			List<MpmCuidadoUsual> listaCuidadosExcluir) throws BaseException  {
		this.getAssociarItensPrescricaoON().salvarAssociacaoCuidadosMedicos(
				itemControle, listaCuidadosIncluir, listaCuidadosExcluir);
	}

	protected AssociarItensPrescricaoON getAssociarItensPrescricaoON() {
		return associarItensPrescricaoON;
	}

	protected EcpItemCtrlCuidadoEnfDAO getItemCtrlCuidadoEnfDAO() {
		return ecpItemCtrlCuidadoEnfDAO;
	}

	protected EcpItemCtrlCuidadoMedicoDAO getItemCtrlCuidadoMedicoDAO() {
		return ecpItemCtrlCuidadoMedicoDAO;
	}
	
	@Override
	public EcpGrupoControle obterGrupoControle(Integer idGrupo) {
		return this.getEcpGrupoControleDAO().obterPorChavePrimaria(idGrupo);
	}

	@Override
	public List<EcpItemControle> buscarItensControlePorPacientePeriodo(
			AipPacientes paciente, Date dataInicial, Date dataFinal, Long trgSeq, DominioTipoGrupoControle... tiposGrupos) {
		return this.getEcpItemControleDAO()
				.pesquisarItemControlePorPacientePeriodo(paciente, dataInicial,
						dataFinal , trgSeq, tiposGrupos);

	}

	@Override
	public EcpItemControle obterItemControlePorId(Short seq) {
		return this.getEcpItemControleDAO().obterPorChavePrimaria(
				seq);
	}

	@Override
	public EcpLimiteItemControle buscaLimiteItemControle(EcpItemControle item,
			DominioUnidadeMedidaIdade medidaIdade, Integer idade) {
		return getEcpLimiteItemControleDAO().buscaLimiteItemControle(item,
				medidaIdade, idade);
	}

}