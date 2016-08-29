package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.EpeCaractDefDiagnostico;
import br.gov.mec.aghu.model.EpeCaractDefDiagnosticoId;
import br.gov.mec.aghu.model.EpeCaractDefinidora;
import br.gov.mec.aghu.model.EpeCuidadoDiagnostico;
import br.gov.mec.aghu.model.EpeCuidadoDiagnosticoId;
import br.gov.mec.aghu.model.EpeCuidadoMedicamento;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeDiagnosticoId;
import br.gov.mec.aghu.model.EpeFatRelDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelDiagnosticoId;
import br.gov.mec.aghu.model.EpeFatRelacionado;
import br.gov.mec.aghu.model.EpeGrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSinCaractDefinidora;
import br.gov.mec.aghu.model.EpeSinCaractDefinidoraId;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasicaId;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCaractDefDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCaractDefinidoraDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCuidadoDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCuidadoMedicamentoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeFatRelDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeFatRelacionadoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeGrupoNecesBasicaDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeSinCaractDefinidoraDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeSubgrupoNecesBasicaDAO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticosSinaisSintomasVO;

/**
 * Porta de entrada do módulo de prescrição de enfermagem.
 * 
 * @author dansantos
 * 
 */

@Modulo(ModuloEnum.PRESCRICAO_ENFERMAGEM)
@Stateless
public class PrescricaoEnfermagemApoioFacade extends BaseFacade implements IPrescricaoEnfermagemApoioFacade {


@Inject
private EpeCaractDefDiagnosticoDAO epeCaractDefDiagnosticoDAO;

@Inject
private EpeFatRelDiagnosticoDAO epeFatRelDiagnosticoDAO;


@EJB
private SubgrupoNecessidadesHumanasCRUD subgrupoNecessidadesHumanasCRUD;

@EJB
private EtiologiaCRUD etiologiaCRUD;

@EJB
private SinaisSintomasCRUD sinaisSintomasCRUD;

@EJB
private EtiologiaDiagnosticoRN etiologiaDiagnosticoRN;

@EJB
private CuidadoDiagnosticoRN cuidadoDiagnosticoRN;

@EJB
private GrupoNecessidadesHumanasCRUD grupoNecessidadesHumanasCRUD;

@EJB
private DiagnosticoCRUD diagnosticoCRUD;

@EJB
private CuidadoMedicamentoRN cuidadoMedicamentoRN;

@EJB
private EpeSinCaractDefinidoraRN epeSinCaractDefinidoraRN;

@EJB
private SinaisSintomasRN sinaisSintomasRN;

@Inject
private EpeDiagnosticoDAO epeDiagnosticoDAO;

@Inject
private EpeCaractDefinidoraDAO epeCaractDefinidoraDAO;

@Inject
private EpeCuidadoMedicamentoDAO epeCuidadoMedicamentoDAO;

@Inject
private EpeSinCaractDefinidoraDAO epeSinCaractDefinidoraDAO;

@Inject
private EpeCuidadoDiagnosticoDAO epeCuidadoDiagnosticoDAO;

@Inject
private EpeFatRelacionadoDAO epeFatRelacionadoDAO;

@Inject
private EpeGrupoNecesBasicaDAO epeGrupoNecesBasicaDAO;

@Inject
private EpeSubgrupoNecesBasicaDAO epeSubgrupoNecesBasicaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1448628938011873332L;
	
	public enum PrescricaoEnfermagemApoioExceptionCode implements BusinessExceptionCode {

		MENSAGEM_SINAIS_SINTOMAS_JA_EXISTENTE
	}

	
	protected EpeFatRelacionadoDAO getEpeFatRelacionadoDAO() {
		return epeFatRelacionadoDAO;
	}	

	@Override
	public Long pesquisarEtiologiasPorSeqDescricaoCount(Short seq, String descricao) {
		return getEpeFatRelacionadoDAO().pesquisarEtiologiasPorSeqDescricaoCount(seq, descricao);
	}

	@Override
	public List<EpeFatRelacionado> pesquisarEtiologiasPorSeqDescricao(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short seq, String descricao) {
		return this.getEpeFatRelacionadoDAO().pesquisarEtiologiasPorSeqDescricao(firstResult, maxResults, orderProperty, asc, seq, descricao);
	}

	@Override
	public void removerEtiologia(Short seq) throws ApplicationBusinessException {
		getEtiologiaCRUD().removerEtiologia(seq);
	}
	
	protected EtiologiaCRUD getEtiologiaCRUD() {
		return etiologiaCRUD;
	}

	protected DiagnosticoCRUD getDiagnosticoCRUD() {
		return diagnosticoCRUD;
	}
	
	protected EpeDiagnosticoDAO getEpeDiagnosticoDAO() {
		return epeDiagnosticoDAO;
	}
	
	@Override
	public void persistirDiagnostico(EpeDiagnostico diagnostico) throws ApplicationBusinessException {
		getDiagnosticoCRUD().persistirDiagnostico(diagnostico);
	}
	
	@Override
	public void excluirDiagnostico(EpeDiagnosticoId diagnosticoId) throws ApplicationBusinessException {
		getDiagnosticoCRUD().excluirDiagnostico(diagnosticoId);
	}

	@Override
	public void persistirEtiologia(EpeFatRelacionado etiologia, Boolean ativo) throws ApplicationBusinessException {
		getEtiologiaCRUD().persistirEtiologia(etiologia, ativo);		
	}

	@Override
	public List<EpeGrupoNecesBasica> pesquisarGrupoNecessidadesHumanasPorSeqDescricaoSituacao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short seqGrupoNecessidadesHumanas,
			String descricaoGrupoNecessidadesHumanas,
			DominioSituacao situacaoGrupoNecessidadesHumanas) {
		return this.getEpeGrupoNecesBasicaDAO().pesquisarGrupoNecessidadesHumanasPorSeqDescricaoSituacao(firstResult, maxResult, orderProperty, asc, seqGrupoNecessidadesHumanas, descricaoGrupoNecessidadesHumanas, situacaoGrupoNecessidadesHumanas);
	}

	@Override
	public Long pesquisarGrupoNecessidadesHumanasPorSeqDescricaoSituacaoCount(
			Short seqGrupoNecessidadesHumanas,
			String descricaoGrupoNecessidadesHumanas,
			DominioSituacao situacaoGrupoNecessidadesHumanas) {
		return this.getEpeGrupoNecesBasicaDAO().pesquisarGrupoNecessidadesHumanasPorSeqDescricaoSituacaoCount(seqGrupoNecessidadesHumanas, descricaoGrupoNecessidadesHumanas, situacaoGrupoNecessidadesHumanas);
	}
	
	@Override
	public List<EpeSubgrupoNecesBasica> pesquisarSubgrupoNecessBasicaOrderSeq(Short gnbSeq){
		return getEpeSubgrupoNecesBasicaDAO().pesquisarSubgrupoNecessBasicaOrderSeq(gnbSeq);
	}

	@Override
	public void removerGrupoNecessidadesHumanas(
			Short seq) throws ApplicationBusinessException {
		grupoNecessidadesHumanasCRUD.removerGrupoNecessidadesHumanas(seq);
	}
	
	protected GrupoNecessidadesHumanasCRUD getGrupoNecessidadesHumanasCRUD() {
		return grupoNecessidadesHumanasCRUD;
	}
	
	protected SubgrupoNecessidadesHumanasCRUD getSubgrupoNecessidadesHumanasCRUD() {
		return subgrupoNecessidadesHumanasCRUD;
	}
	
	@Override
	public void persistirGrupoNecessidadesHumanas(EpeGrupoNecesBasica grupoNecessidadesBasicas, Boolean ativo) throws ApplicationBusinessException {
		getGrupoNecessidadesHumanasCRUD().persistirGrupoNecessidadesHumanas(grupoNecessidadesBasicas, ativo);		
	}
	
	@Override
	public void persistirSubgrupoNecessidadesHumanas(EpeSubgrupoNecesBasica epeSubgrupoNecesBasica, Boolean checkboxSubGrupoAtivo, Short seqGrupoNecessidadesHumanas) throws ApplicationBusinessException {
		getSubgrupoNecessidadesHumanasCRUD().persistirSubgrupoNecessidadesHumanas(epeSubgrupoNecesBasica, checkboxSubGrupoAtivo, seqGrupoNecessidadesHumanas);
	}
	
	@Override
	public void removerSubgrupoNecessidadesHumanas(EpeSubgrupoNecesBasicaId id) throws ApplicationBusinessException {
		subgrupoNecessidadesHumanasCRUD.removerSubgrupoNecessidadesHumanas(id);
	}
	
	protected EpeGrupoNecesBasicaDAO getEpeGrupoNecesBasicaDAO() {
		return epeGrupoNecesBasicaDAO;
	}
	
	protected EpeSubgrupoNecesBasicaDAO getEpeSubgrupoNecesBasicaDAO() {
		return epeSubgrupoNecesBasicaDAO;
	}
	
	public EpeCaractDefinidoraDAO getEpeCaractDefinidoraDAO() {
		return epeCaractDefinidoraDAO;
	}
	
	@Override
	public EpeCaractDefinidora obterSinaisSintomasPorCodigo(Integer codigo) {
		return getEpeCaractDefinidoraDAO().obterPorChavePrimaria(codigo);
	}

	@Override
	public void removerSinaisSintomas(Integer codigo) {
		getEpeCaractDefinidoraDAO().removerPorId(codigo);
	}

	@Override
	public List<EpeCaractDefinidora> listarSinaisSintomasPorCodigoDescricaoSituacao(Integer firstResult, Integer maxResult, String orderProperty, Boolean asc, Integer codigo, String descricao, DominioSituacao situacao) {
		return getEpeCaractDefinidoraDAO().listarSinaisSintomasPorCodigoDescricaoSituacao(firstResult, maxResult, orderProperty, asc, codigo, descricao, situacao);
	}

	@Override
	public Long listarSinaisSintomasPorCodigoDescricaoSituacaoCount(Integer codigo, String descricao, DominioSituacao situacao) {
		return getEpeCaractDefinidoraDAO().listarSinaisSintomasPorCodigoDescricaoSituacaoCount(codigo, descricao, situacao);
	}

	@Override
	public void persistirSinaisSintomas(EpeCaractDefinidora sinaisSintomas) throws ApplicationBusinessException{
		getSinaisSintomasCRUD().persistir(sinaisSintomas);
	}

	private SinaisSintomasCRUD getSinaisSintomasCRUD() {
		return sinaisSintomasCRUD;
	}
	
	@Override
	public void verificaExclusaoEpeCaractDefinidora(EpeCaractDefinidora epeCaractDefinidora) throws ApplicationBusinessException {
		getSinaisSintomasCRUD().verificaExclusaoEpeCaractDefinidora(epeCaractDefinidora);
	}

	private SinaisSintomasRN getSinaisSintomasRN() {
		return sinaisSintomasRN;
	}

	@Override
	public void verificaAlteracaoEpeCaractDefinidora(EpeCaractDefinidora epeCaractDefinidora) throws ApplicationBusinessException {
		getSinaisSintomasRN().verificaAlteracaoEpeCaractDefinidora(epeCaractDefinidora);
	}
	
	protected EpeSinCaractDefinidoraDAO getEpeSinCaractDefinidoraDAO() {
		return epeSinCaractDefinidoraDAO;
	}
	
	@Override
	public List<EpeSinCaractDefinidora> buscarSinonimoSinaisSintomas(Integer cdeCodigo, Integer firstResult, Integer maxResult, String orderProperty, boolean asc){
		return getEpeSinCaractDefinidoraDAO().buscarSinonimoSinaisSintomas(cdeCodigo, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public Long buscarSinonimoSinaisSintomasCount(Integer cdeCodigo){
		return getEpeSinCaractDefinidoraDAO().buscarSinonimoSinaisSintomasCount(cdeCodigo);
	}
	
	@Override
	public List<EpeCaractDefinidora> pesquisarCaracteristicasDefinidoras(Object objSinaisSintomas, List<EpeSinCaractDefinidora> listaSinonimos){
		return getEpeCaractDefinidoraDAO().pesquisarCaracteristicasDefinidoras(objSinaisSintomas, listaSinonimos);
	}
	
	private EpeSinCaractDefinidoraRN getEpeSinCaractDefinidoraRN() {
		return epeSinCaractDefinidoraRN;
	}
	
	@Override
	public void inserirEpeSinCaractDefinidora(EpeSinCaractDefinidora novaCaracteristicaDefinidora) throws ApplicationBusinessException{
		getEpeSinCaractDefinidoraRN().inserirEpeSinCaractDefinidora(novaCaracteristicaDefinidora);
	}
	
	@Override
	public String removerEpeSinCaractDefinidora(EpeSinCaractDefinidoraId id){
		getEpeSinCaractDefinidoraRN().removerEpeSinCaractDefinidora(id);
		return "MENSAGEM_EXCLUSAO_SINONIMO_SINAIS_SINTOMAS";
	}
	
	public List<EpeFatRelacionado> pesquisarEtiologiasTodas(String parametro) {
		return this.getEpeFatRelacionadoDAO().pesquisarEtiologias(parametro);
	}
	
	public Long pesquisarEtiologiasCountTodas(String parametro) {
		return getEpeFatRelacionadoDAO().pesquisarEtiologiasCount(parametro);
	}
	
	public EpeFatRelacionado obterEpeFatRelacionadoPorChavePrimaria(Short chavePrimaria) {
		return getEpeFatRelacionadoDAO().obterPorChavePrimaria(chavePrimaria);
	}
	
	protected EpeCuidadoDiagnosticoDAO getEpeCuidadoDiagnosticoDAO() {
		return epeCuidadoDiagnosticoDAO;
	}

	protected CuidadoDiagnosticoRN getCuidadoDiagnosticoRN() {
		return cuidadoDiagnosticoRN;
	}
	
	public void persistirCuidadoDiagnostico(EpeCuidadoDiagnostico cuidadodiagnostico) throws ApplicationBusinessException {
		cuidadoDiagnosticoRN.prePersistirValidarEpeCuidados(cuidadodiagnostico.getCuidado());
		epeCuidadoDiagnosticoDAO.persistir(cuidadodiagnostico);
	}
	
	public void atualizarCuidadoDiagnostico(EpeCuidadoDiagnostico cuidadodiagnostico) throws ApplicationBusinessException {
		epeCuidadoDiagnosticoDAO.merge(cuidadodiagnostico);
	}
	
	public void excluirCuidadoDiagnostico(EpeCuidadoDiagnosticoId id) throws ApplicationBusinessException {
		EpeCuidadoDiagnostico cuidadoDiagnostico = epeCuidadoDiagnosticoDAO.obterPorChavePrimaria(id);
		cuidadoDiagnosticoRN.preRemoverValidarCuidadoRelacionadoPrescricao(cuidadoDiagnostico.getCuidado().getSeq(),
				cuidadoDiagnostico.getId().getFdgFreSeq(), cuidadoDiagnostico.getId().getFdgDgnSequencia(), cuidadoDiagnostico.getId().getFdgDgnSnbSequencia(),
				cuidadoDiagnostico.getId().getFdgDgnSnbGnbSeq());
		getEpeCuidadoDiagnosticoDAO().removerPorId(id);
	}

	protected CuidadoMedicamentoRN getCuidadoMedicamentoRN() {
		return cuidadoMedicamentoRN;
	}

	public List<AfaMedicamento> pesquisarMedicamentosParaMedicamentosCuidados(String parametro) {
		return getCuidadoMedicamentoRN().pesquisarMedicamentosParaMedicamentosCuidados(parametro);
	}

	public Long pesquisarMedicamentosParaMedicamentosCuidadosCount(String parametro) {
		return getCuidadoMedicamentoRN().pesquisarMedicamentosParaMedicamentosCuidadosCount(parametro);
	}

	public List<AfaMedicamento> pesquisarMedicamentosParaListagemMedicamentosCuidados(Integer matCodigo, DominioSituacaoMedicamento indSituacao,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getCuidadoMedicamentoRN().pesquisarMedicamentosParaListagemMedicamentosCuidados(matCodigo, indSituacao, firstResult, maxResult, orderProperty,
				asc);
	}

	public Long pesquisarMedicamentosParaListagemMedicamentosCuidadosCount(Integer matCodigo, DominioSituacaoMedicamento indSituacao) {
		return getCuidadoMedicamentoRN().pesquisarMedicamentosParaListagemMedicamentosCuidadosCount(matCodigo, indSituacao);
	}

	public AfaMedicamento obterMedicamentoPorMatCodigo(Integer matCodigo) {
		return getCuidadoMedicamentoRN().obterMedicamentoPorMatCodigo(matCodigo);
	}

	protected EpeCuidadoMedicamentoDAO getEpeCuidadoMedicamentoDAO() {
		return epeCuidadoMedicamentoDAO;
	}
	
	public void persistirCuidadoMedicamento(EpeCuidadoMedicamento cuidadoMedicamento) throws ApplicationBusinessException {
		getCuidadoMedicamentoRN().prePersistirValidarHoras(cuidadoMedicamento.getHorasAntes(), cuidadoMedicamento.getHorasApos());
		getEpeCuidadoMedicamentoDAO().persistir(cuidadoMedicamento);
	}

	public void atualizarCuidadoMedicamento(EpeCuidadoMedicamento cuidadoMedicamento) throws ApplicationBusinessException {
		getCuidadoMedicamentoRN().prePersistirValidarHoras(cuidadoMedicamento.getHorasAntes(), cuidadoMedicamento.getHorasApos());

		EpeCuidadoMedicamento cuidadoMedicamentoBase = getEpeCuidadoMedicamentoDAO().obterPorCuiSeqMatCodigo(cuidadoMedicamento.getCuidado().getSeq(),
				cuidadoMedicamento.getMedicamento().getMatCodigo());
		cuidadoMedicamento.setId(cuidadoMedicamentoBase.getId());
		cuidadoMedicamento.setVersion(cuidadoMedicamentoBase.getVersion());

		getEpeCuidadoMedicamentoDAO().merge(cuidadoMedicamento);
	}
	
	public void excluirCuidadoMedicamento(Short cuiSeq, Integer medMatCodigo) throws ApplicationBusinessException {
		getCuidadoMedicamentoRN().preRemoverValidarCuidadoRelacionadoPrescricao(cuiSeq, medMatCodigo);
		EpeCuidadoMedicamento cuidadoMedicamento = getEpeCuidadoMedicamentoDAO().obterPorCuiSeqMatCodigo(cuiSeq, medMatCodigo); 
		getEpeCuidadoMedicamentoDAO().remover(cuidadoMedicamento);
	}
	
	protected EpeFatRelDiagnosticoDAO getEpeFatRelDiagnosticoDAO() {
		return epeFatRelDiagnosticoDAO;
	}

	@Override
	public List<DiagnosticosSinaisSintomasVO> obterDiagnosticosGrupoSubgrupoNecessidadesBasicas(Short gnbSeq, Short snbSequencia, Short dngSequencia, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getEpeFatRelDiagnosticoDAO().obterDiagnosticosGrupoSubgrupoNecessidadesBasicas(gnbSeq, snbSequencia, dngSequencia, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long obterDiagnosticosGrupoSubgrupoNecessidadesBasicasCount(Short gnbSeq, Short snbSequencia, Short dngSequencia) {
		return getEpeFatRelDiagnosticoDAO().obterDiagnosticosGrupoSubgrupoNecessidadesBasicasCount(gnbSeq, snbSequencia, dngSequencia);
	}

	protected EpeCaractDefDiagnosticoDAO getEpeCaractDefDiagnosticoDAO() {
		return epeCaractDefDiagnosticoDAO;
	}

	@Override
	public List<EpeCaractDefDiagnostico> pesquisarCaractDefDiagnosticoPorSubgrupo(Short snbGnbSeq, Short snbSequencia, Short sequencia, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getEpeCaractDefDiagnosticoDAO().pesquisarCaractDefDiagnosticoPorSubgrupo(snbGnbSeq, snbSequencia, sequencia, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long pesquisarCaractDefDiagnosticoPorSubgrupoCount(Short snbGnbSeq, Short snbSequencia, Short sequencia) {
		return getEpeCaractDefDiagnosticoDAO().pesquisarCaractDefDiagnosticoPorSubgrupoCount(snbGnbSeq, snbSequencia, sequencia);
	}

	@Override
	public List<EpeCaractDefinidora> pesquisarCaracteristicasDefinidorasNaoAtribuidasDiagnostico(Object filtro, Short snbGnbSeq, Short snbSequencia, Short sequencia){
		return getEpeCaractDefinidoraDAO().pesquisarCaracteristicasDefinidorasNaoAtribuidasDiagnostico(filtro, snbGnbSeq, snbSequencia, sequencia);
	}

	@Override
	public Long pesquisarCaracteristicasDefinidorasNaoAtribuidasDiagnosticoCount(Object filtro, Short snbGnbSeq, Short snbSequencia, Short sequencia) {
		return getEpeCaractDefinidoraDAO().pesquisarCaracteristicasDefinidorasNaoAtribuidasDiagnosticoCount(filtro, snbGnbSeq, snbSequencia, sequencia);
	}

	@Override
	public void excluirEpeCaractDefDiagnostico(EpeCaractDefDiagnosticoId id) {
		epeCaractDefDiagnosticoDAO.removerPorId(id);
	}

	@Override
	public void inserirEpeCaractDefDiagnostico(EpeCaractDefDiagnostico epeCaractDefDiagnostico) {
		getEpeCaractDefDiagnosticoDAO().persistir(epeCaractDefDiagnostico);
	}
	
	@Override
	public void persistirEtiologiaDiagnostico(EpeFatRelDiagnostico etiologiaDiagnostico)  throws ApplicationBusinessException {
		getEtiologiaDiagnosticoRN().prePersistirEtiologiaDiagnostico(etiologiaDiagnostico);
	}

	@Override
	public void excluirEtiologiaDiagnostico(EpeFatRelDiagnosticoId id)  throws ApplicationBusinessException {
		etiologiaDiagnosticoRN.excluirEtiologiaDiagnostico(id);
	}
	
	protected EtiologiaDiagnosticoRN getEtiologiaDiagnosticoRN() {
		return etiologiaDiagnosticoRN;
	}

	@Override
	public void ativarDesativarEtiologiaDiagnostico(EpeFatRelDiagnostico epeEtiologiaDiagnostico) throws ApplicationBusinessException {
		getEtiologiaDiagnosticoRN().ativarDesativarEtiologiaDiagnostico(epeEtiologiaDiagnostico);
		
	}

	@Override
	public List<EpeFatRelacionado> pesquisarEtiologiasNaoRelacionadas(String filtro, Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia) {
		return getEpeFatRelacionadoDAO().pesquisarEtiologiasNaoRelacionadas(filtro, dgnSnbGnbSeq, dgnSnbSequencia, dgnSequencia);
	}

	@Override
	public Long pesquisarEtiologiasNaoRelacionadasCount(String filtro, Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia) {
		return getEpeFatRelacionadoDAO().pesquisarEtiologiasNaoRelacionadasCount(filtro, dgnSnbGnbSeq, dgnSnbSequencia, dgnSequencia);
	}
}