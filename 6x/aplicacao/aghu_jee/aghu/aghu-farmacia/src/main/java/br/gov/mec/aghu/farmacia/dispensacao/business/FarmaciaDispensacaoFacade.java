package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescritoDispensacaoMdto;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.dao.AfaPrescricaoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoOcorDispensacaoDAO;
import br.gov.mec.aghu.farmacia.vo.AfaDispensacaoMdtoVO;
import br.gov.mec.aghu.farmacia.vo.ConsultaDispensacaoMedicamentosVO;
import br.gov.mec.aghu.farmacia.vo.DispensacaoMedicamentosVO;
import br.gov.mec.aghu.farmacia.vo.ListaOcorrenciaVO;
import br.gov.mec.aghu.farmacia.vo.TicketMdtoDispensadoVO;
import br.gov.mec.aghu.model.AfaDispMdtoCbSps;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaPrescricaoMedicamento;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAfaPrcrDispMdtos;
import br.gov.mec.aghu.prescricaomedica.vo.DispensacaoFarmaciaVO;

@Modulo(ModuloEnum.FARMACIA)
@Stateless
public class FarmaciaDispensacaoFacade extends BaseFacade implements IFarmaciaDispensacaoFacade {

	@EJB
	private PesquisarPacientesParaDispensacaoON pesquisarPacientesParaDispensacaoON;

	@EJB
	private RealizarTriagemMedicamentosPrescricaoON realizarTriagemMedicamentosPrescricaoON;

	@EJB
	private MovimentacaoTriagemDispensacaoMdtosON movimentacaoTriagemDispensacaoMdtosON;

	@EJB
	private MedicamentosDispensacaoON medicamentosDispensacaoON;

	@EJB
	private DispensaMedicamentoRN dispensaMedicamentoRN;

	@EJB
	private EstornaMedicamentoDispensadoRN estornaMedicamentoDispensadoRN;

	@EJB
	private AfaDispMdtoCbSpsRN afaDispMdtoCbSpsRN;

	@EJB
	private DispensaMedicamentoON dispensaMedicamentoON;

	@EJB
	private TratarOcorrenciasON tratarOcorrenciasON;

	@EJB
	private DispensacaoMdtosCodBarrasRN dispensacaoMdtosCodBarrasRN;

	@EJB
	private EstornaMedicamentoDispensadoON estornaMedicamentoDispensadoON;

	@EJB
	private ImprimirTicketDispensacaoMedicamentoON imprimirTicketDispensacaoMedicamentoON;

	@EJB
	private AlterarDispensacaoDeMedicamentosON alterarDispensacaoDeMedicamentosON;

	@EJB
	private DispensacaoMdtosCodBarrasON dispensacaoMdtosCodBarrasON;

	@EJB
	private DispensacaoDePrescricaoNaoEletronicaON dispensacaoDePrescricaoNaoEletronicaON;

	@Inject
	private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO;

	@Inject
	private AfaTipoOcorDispensacaoDAO afaTipoOcorDispensacaoDAO;

	@Inject
	private AfaPrescricaoMedicamentoDAO afaPrescricaoMedicamentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7611341848260773541L;

	@Override
	@BypassInactiveModule
	public List<DispensacaoMedicamentosVO> pesquisarDispensacaoMdtos(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, MpmPrescricaoMedica prescricaoMedica, Short unfSeq, Long seqPrescricaoNaoEletronica) {
		return getMedicamentosDispensacaoON().montarListaDispensacaoMedicamentoVO(firstResult, maxResult, 
				orderProperty, asc, prescricaoMedica, unfSeq, seqPrescricaoNaoEletronica);
	}

	@Override
	@BypassInactiveModule
	public List<AghUnidadesFuncionais> listarFarmaciasAtivasByPesquisa(
			Object strPesquisa) {
		return getPesquisarPacientesParaDispensacaoON().listarFarmaciasAtivasByPesquisa(strPesquisa);
	}
	
	@Override
	@BypassInactiveModule
	public Long listarFarmaciasAtivasByPesquisaCount(Object strPesquisa) {
		return getPesquisarPacientesParaDispensacaoON().listarFarmaciasAtivasByPesquisaCount(strPesquisa);
	}
	
	@Override
	@BypassInactiveModule
	public List<VAfaPrcrDispMdtos> pesquisarPacientesParaDispensacao(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Date dataReferencia, AghUnidadesFuncionais farmacia,
			AghUnidadesFuncionais unidadeFuncionalPrescricao, AinLeitos leito,
			AinQuartos quarto, AipPacientes paciente) throws ApplicationBusinessException {
		return getPesquisarPacientesParaDispensacaoON().pesquisarPacientesParaDispensacao(firstResult, maxResult,
						orderProperty, asc, dataReferencia, farmacia,
						unidadeFuncionalPrescricao, leito, quarto, paciente);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarPacientesParaDispensacaoCount(Date dataReferencia,
			AghUnidadesFuncionais farmacia,
			AghUnidadesFuncionais unidadeFuncionalPrescricao, AinLeitos leito,
			AinQuartos quarto, AipPacientes paciente) {
		return getPesquisarPacientesParaDispensacaoON()
				.pesquisarPacientesParaDispensacaoCount(dataReferencia,
						farmacia, unidadeFuncionalPrescricao, leito, quarto,
						paciente);
	}
	
	// Estória #5387
	@Override
	@BypassInactiveModule
	public List<AfaDispensacaoMdtos> pesquisarItensDispensacaoMdtos(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AghUnidadesFuncionais unidadeSolicitante, Integer prontuario, String nomePaciente, Date dtHrInclusaoItem, AfaMedicamento medicamento, 
			DominioSituacaoDispensacaoMdto situacao, AghUnidadesFuncionais farmacia, AghAtendimentos aghAtendimentos, String etiqueta, 
			MpmPrescricaoMedica prescricaoMedica, String loteCodigo, Boolean indPmNaoEletronica) {
		return getMedicamentosDispensacaoON().pesquisarItensDispensadosPorFiltro(firstResult, maxResult, orderProperty, asc, unidadeSolicitante, prontuario, nomePaciente, 
				dtHrInclusaoItem, medicamento, situacao, farmacia, aghAtendimentos, etiqueta, prescricaoMedica, loteCodigo, indPmNaoEletronica);
	}
	
	// Estória #5387
	@Override
	@BypassInactiveModule
	public Long pesquisarItensDispensacaoMdtosCount(
			AghUnidadesFuncionais unidadeSolicitante, Integer prontuario,
			String nomePaciente, Date dtHrInclusaoItem,
			AfaMedicamento medicamento,
			DominioSituacaoDispensacaoMdto situacao,
			AghUnidadesFuncionais farmacia, AghAtendimentos aghAtendimentos,
			String etiqueta, MpmPrescricaoMedica prescricaoMedica, String loteCodigo, Boolean indPmNaoEletronica) {

		return getMedicamentosDispensacaoON().pesquisarItensDispensacaoMdtosCount(unidadeSolicitante, prontuario, nomePaciente, 
				dtHrInclusaoItem, medicamento, situacao, farmacia, aghAtendimentos, etiqueta, prescricaoMedica, loteCodigo, indPmNaoEletronica);
	}
	
	//#5388 
	@Override
	public List<MpmItemPrescricaoMdto> recuperarListaTriagemMedicamentosPrescritos(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, MpmPrescricaoMedica prescricaoMedica,
			AfaMedicamento medicamento) {
		return getMovimentacaoTriagemDispensacaoMdtosON()
				.recuperarListaTriagemMedicamentosPrescritos(firstResult,
						maxResult, orderProperty, asc, prescricaoMedica,
						medicamento);
	}

	@Override
	@BypassInactiveModule
	public List<AfaDispensacaoMdtos> recuperarListaDispensacaoMedicamentos(Date dataDeReferenciaInicio, Date dataDeReferenciaFim, 
			AfaMedicamento medicamento, AipPacientes paciente, boolean objectAtachado, Long seqAfaDispSelecionadaCheckBox) throws ApplicationBusinessException {
		return getEstornarMedicamentoDispensadoON().recuperarListaDispensacaoMedicamentos(
				dataDeReferenciaInicio, dataDeReferenciaFim, medicamento, paciente, objectAtachado, seqAfaDispSelecionadaCheckBox);
	}
	
//	public List<AipPacientes> pesquisarPacienteComAtendimentoEOrigemByProntuario(Integer nroProntuario) {
//		
//		return getEstornaMedicamentoDispensadoON().pesquisarPacienteComAtendimentoEOrigemByProntuario(nroProntuario);
//	}
	
	@Override
	@BypassInactiveModule
	public void estornarMedicamentoDispensadoByEtiquetaComCb(String pEtiqueta, String nomeMicrocomputador) throws BaseException {
		getEstornarMedicamentoDispensadoON().estornarMedicamentoDispensadoByEtiquetaComCb(pEtiqueta, nomeMicrocomputador);
	}

	@Override
	@BypassInactiveModule
	public void validarDatas(Date dataDeReferenciaInicio, Date dataDeReferenciaFim) throws ApplicationBusinessException {
		getEstornarMedicamentoDispensadoON().validarDatas(dataDeReferenciaInicio, dataDeReferenciaFim);

	}
	
	@Override
	@BypassInactiveModule
	public void processaAfaTipoOcorBySeqInAfaDispMdtoEstorno(AfaDispensacaoMdtos adm, Short seqMotivoEstornoPrescricaNaoEletronica) throws ApplicationBusinessException {
		getEstornarMedicamentoDispensadoON().processaAfaTipoOcorBySeqInAfaDispMdtoEstorno(adm,seqMotivoEstornoPrescricaNaoEletronica);
	}
	@Override
	@BypassInactiveModule
	public void realizaEstornoMedicamentoDispensadoDaLista(
			List<AfaDispensacaoMdtos> listaDispensacaoModificada,
			List<AfaDispensacaoMdtos> listaDispensacaoOriginal,
			String nomeMicrocomputador) throws BaseException {
		getEstornarMedicamentoDispensadoON().realizaEstornoMedicamentoDispensadoDaLista(listaDispensacaoModificada, listaDispensacaoOriginal, nomeMicrocomputador);
	}

	@Override
	@BypassInactiveModule
	public List<AfaDispensacaoMdtos> recuperarListaTriagemMedicamentosPrescricao(
			MpmPrescricaoMedica prescricaoMedica) {
		return getRealizarTriagemMedicamentosPrescricaoON().recuperarListaTriagemMedicamentosPrescricao(prescricaoMedica);
	}

	@Override
	@BypassInactiveModule
	public List<AfaTipoOcorDispensacao> pesquisarTipoOcorrenciasAtivasENaoEstornada() {
		return getAfaTipoDispensacaoDAO().pesquisarTipoOcorrenciasAtivasENaoEstornada();
	}

	@Override
	@BypassInactiveModule
	public Boolean processaTipoOcorrenciaParaListaMedicamentosPrescricao(
			List<AfaDispensacaoMdtos> listaTriagem) throws ApplicationBusinessException {
		return getRealizarTriagemMedicamentosPrescricaoON().processaTipoOcorrenciaParaListaMedicamentosPrescricao(listaTriagem);
	}

	@Override
	@BypassInactiveModule
	public List<AfaTipoOcorDispensacao> pesquisarTodosMotivosOcorrenciaDispensacao(
			Object strPesquisa) {
		return getAfaTipoDispensacaoDAO().pesquisarTodosMotivosOcorrenciaDispensacao(strPesquisa);
	}
	
	@Override
	@BypassInactiveModule
	public Long pesquisarTodosMotivosOcorrenciaDispensacaoCount(Object strPesquisa) {
		return getAfaTipoDispensacaoDAO().pesquisarTodosMotivosOcorrenciaDispensacaoCount(strPesquisa);
	}
	
	@Override
	@BypassInactiveModule
	public void realizaTriagemMedicamentoPrescricao(
			List<AfaDispensacaoMdtos> listaTriagemModificada,
			List<AfaDispensacaoMdtos> listaTriagemOriginal,
			String nomeMicrocomputador) throws BaseException {
		getRealizarTriagemMedicamentosPrescricaoON().realizaTriagemMedicamentoPrescricao(listaTriagemModificada, listaTriagemOriginal, nomeMicrocomputador);
	}
	
	// Tratar Ocorrências - 5344

	@Override
	@BypassInactiveModule
	public List<AfaDispensacaoMdtos> recuperarlistaOcorrenciasMdtosDispensados(AghUnidadesFuncionais unidadeFuncional, Date data,
			AfaTipoOcorDispensacao tipoOcorDispensacao, Integer prontuario, AinLeitos leito, AghUnidadesFuncionais farmacia) throws ApplicationBusinessException {
		return getTratarOcorrenciasON().recuperarlistaOcorrenciasMdtosDispensados(unidadeFuncional,data,tipoOcorDispensacao,prontuario,leito,farmacia);
	}

	@Override
	@BypassInactiveModule
	public void assinaDispMdtoSemUsoDeEtiqueta(AfaDispensacaoMdtos adm, AghMicrocomputador micro, AghMicrocomputador microDispensador, String nomeMicrocomputador) throws BaseException {
		getTratarOcorrenciasON().assinaDispMdtoSemUsoDeEtiqueta(adm, micro, microDispensador, nomeMicrocomputador);
	}

	@Override
	@BypassInactiveModule
	public void pesquisaEtiquetaComCbMedicamento(String etiqueta,
			List<AfaDispensacaoMdtos> listaOcorrenciasMdtosDispensados,
			AghUnidadesFuncionais farmacia, AghMicrocomputador micro, AghMicrocomputador microDispensador) throws BaseException {
		getTratarOcorrenciasON().pesquisaEtiquetaComCbMedicamento(etiqueta,
				listaOcorrenciasMdtosDispensados, farmacia, micro, microDispensador);
	}
	
	@Override
	@BypassInactiveModule
	public List<AfaDispensacaoMdtos> pesquisarDispensacaoMdtoPorItemPrescrito(
			MpmItemPrescricaoMdto itemPrescrito,
			MpmPrescricaoMedica prescricaoMedica, Boolean b) {
		return getMovimentacaoTriagemDispensacaoMdtosON().pesquisarDispensacaoMdtoPorItemPrescrito(itemPrescrito, prescricaoMedica, b);
	}

	@Override
	@BypassInactiveModule
	public List<AfaDispensacaoMdtos> criarListaNovaMovimentacao() {
		return getMovimentacaoTriagemDispensacaoMdtosON().criarListaNovaMovimentacao();
	}

	@Override
	@BypassInactiveModule
	public String recuperaDescricaoMdtoPrescrito(
			MpmPrescricaoMedica prescricaoMedica,
			MpmItemPrescricaoMdto itemPrescrito) {
		return getMovimentacaoTriagemDispensacaoMdtosON().recuperaDescricaoMdtoPrescrito(prescricaoMedica, itemPrescrito);
	}

	@Override
	@BypassInactiveModule
	public DominioSituacaoItemPrescritoDispensacaoMdto verificarSituacaoItemPrescritoIncluidoFarmacia(
			AfaDispensacaoMdtos selecao) {
		return getMovimentacaoTriagemDispensacaoMdtosON().verificarSituacaoItemPrescritoIncluidoFarmacia(selecao);
	}

	@Override
	@BypassInactiveModule
	public void persistirMovimentoDispensacaoMdtos(
			MpmPrescricaoMedica prescricaoMedica,
			MpmItemPrescricaoMdto itemPrescrito,
			List<AfaDispensacaoMdtos> medicamentosDispensadosModificados,
			List<AfaDispensacaoMdtos> medicamentosDispensadosOriginal,
			String nomeMicrocomputador) throws BaseException {
		getMovimentacaoTriagemDispensacaoMdtosON()
				.persistirMovimentoDispensacaoMdtos(prescricaoMedica,
						itemPrescrito, medicamentosDispensadosModificados,
						medicamentosDispensadosOriginal, nomeMicrocomputador);
		
	}
	
	@Override
	@BypassInactiveModule
	public Short getLocalDispensa2(Integer atendimentoSeq, Integer medMatCodigo, BigDecimal dose, Integer fdsSeq, Short farmPadrao) throws ApplicationBusinessException{
		return getDispensaMedicamentoRN().getLocalDispensa2(atendimentoSeq, medMatCodigo, dose, fdsSeq, farmPadrao);
	}
	
	@Override
	@BypassInactiveModule
	public void dispensarMedicamentoByEtiquetaComCb(String nroEtiqueta, List<AfaDispensacaoMdtos> listaMdtosPrescritos, AghMicrocomputador microDispensador, String nomeMicrocomputador) throws BaseException {
		getDispensacaoMdtosCodBarrasON().dispensarMdtoCodBarras(nroEtiqueta, listaMdtosPrescritos, microDispensador, nomeMicrocomputador);
	}

	
	@Override
	@BypassInactiveModule
	public String validarCodigoBarrasEtiqueta(final String nroEtiqueta){
		return getDispensacaoMdtosCodBarrasRN().validarCodigoBarrasEtiqueta(nroEtiqueta);
	}
	
	@Override
	@BypassInactiveModule
	public void assinaDispensarMdtoQtdeTotalSemEtiqueta(AfaDispensacaoMdtos admNew, String nomeMicrocomputador) throws BaseException {
		getDispensacaoMdtosCodBarrasON().assinaDispMdtoSemUsoDeEtiqueta(admNew, nomeMicrocomputador);
	}
	
	@Override
	@BypassInactiveModule
	public Boolean verificaDispensacaoAntesDeEvento(Integer pmeAtdSeq, Integer pmeSeq, AghUnidadesFuncionais unidadeFuncionalMicro) {
		return getDispensacaoMdtosCodBarrasON().verificaDispensacaoAntesDeEvento(pmeAtdSeq, pmeSeq, unidadeFuncionalMicro);
	}
	
	@Override
	@BypassInactiveModule
	public List<DispensacaoFarmaciaVO> popularDispensacaoFarmaciaVO(
			MpmPrescricaoMedicaId prescricaoMedicaId,
			List<MpmPrescricaoMdto> listaPrescricaoMdto, Boolean alterado)
			throws BaseException {
		return this.getDispensaMedicamentoON().popularDispensacaoFarmaciaVO(
				prescricaoMedicaId, listaPrescricaoMdto, alterado);
	}

	@Override
	@BypassInactiveModule
	public List<MpmPrescricaoMdto> buscarPrescricaoMedicamentos(
			MpmPrescricaoMedicaId prescricaoMedicaId)
			throws ApplicationBusinessException {
		return this.getDispensaMedicamentoON().buscarPrescricaoMedicamentos(
				prescricaoMedicaId);
	}
	
	@Override
	@BypassInactiveModule
	public List<MpmPrescricaoMdto> buscarPrescricaoMedicamentosConfirmados(
			MpmPrescricaoMedicaId prescricaoMedicaId)
			throws ApplicationBusinessException {
		return this.getDispensaMedicamentoON()
				.buscarPrescricaoMedicamentosConfirmados(prescricaoMedicaId);
	}
	
	@Override
	@BypassInactiveModule
	public List<AghUnidadesFuncionais> listarFarmacias() {
		return this.getDispensaMedicamentoON().listarFarmacias();
	}

	@Override
	@BypassInactiveModule
	public List<DispensacaoFarmaciaVO> ordenarDispensacaoFarmaciaVO(
			List<DispensacaoFarmaciaVO> lista) {
		return this.getDispensaMedicamentoON().ordenarDispensacaoFarmaciaVO(
				lista);
	}
	
	@Override
	@BypassInactiveModule
	public void persisteDispMdtoCbSps(AfaDispMdtoCbSps dmc, String nomeMicrocomputador) throws ApplicationBusinessException {
		getAfaDispMdtoCbSpsRN().persisteDispMdtoCbSps(dmc, nomeMicrocomputador);
	}
	
	//Getters
	
	private DispensaMedicamentoRN getDispensaMedicamentoRN(){
		return dispensaMedicamentoRN;
	}
	
	private AfaTipoOcorDispensacaoDAO getAfaTipoDispensacaoDAO(){
		return afaTipoOcorDispensacaoDAO;
	}
	private ImprimirTicketDispensacaoMedicamentoON getImprimirTicketDispensacaoMedicamentoON(){
		return imprimirTicketDispensacaoMedicamentoON;
	}
	
	private AfaDispMdtoCbSpsRN getAfaDispMdtoCbSpsRN(){
		return afaDispMdtoCbSpsRN;
	}
	
	private DispensaMedicamentoON getDispensaMedicamentoON() {
		return dispensaMedicamentoON;
	}
	
	private TratarOcorrenciasON getTratarOcorrenciasON(){
		return tratarOcorrenciasON;
	}
	
	private RealizarTriagemMedicamentosPrescricaoON getRealizarTriagemMedicamentosPrescricaoON(){
		return realizarTriagemMedicamentosPrescricaoON;
	}
	
	private EstornaMedicamentoDispensadoON getEstornaMedicamentoDispensadoON() {
		return estornaMedicamentoDispensadoON;
	}
	
	private EstornaMedicamentoDispensadoON getEstornarMedicamentoDispensadoON(){
		return estornaMedicamentoDispensadoON;
	}
	
	private MovimentacaoTriagemDispensacaoMdtosON getMovimentacaoTriagemDispensacaoMdtosON() {
		return movimentacaoTriagemDispensacaoMdtosON;
	}
	
	private PesquisarPacientesParaDispensacaoON getPesquisarPacientesParaDispensacaoON(){
		return pesquisarPacientesParaDispensacaoON;
	}
	
	private MedicamentosDispensacaoON getMedicamentosDispensacaoON() {
		return medicamentosDispensacaoON;
	}
	
	private DispensacaoMdtosCodBarrasRN getDispensacaoMdtosCodBarrasRN() {
		return dispensacaoMdtosCodBarrasRN;
	}
	
	private DispensacaoMdtosCodBarrasON getDispensacaoMdtosCodBarrasON() {
		return dispensacaoMdtosCodBarrasON;
	}
	
	@Override
	@BypassInactiveModule
	public List<AfaDispensacaoMdtos> pesquisarListaDispMdtoDispensarPelaPrescricao(
			Integer pmeAtdSeq, Integer pmeSeq, AghUnidadesFuncionais unidadeFuncionalMicro) {
		return getDispensacaoMdtosCodBarrasON()
				.pesquisarListaDispMdtoDispensarPelaPrescricao(pmeAtdSeq,
						pmeSeq, unidadeFuncionalMicro);
	}

	@Override
	@BypassInactiveModule
	public void processaAfaTipoOcorBySeqInAfaDispMdto(AfaDispensacaoMdtos adm) throws ApplicationBusinessException {
		getRealizarTriagemMedicamentosPrescricaoON().processaAfaTipoOcorBySeqInAfaDispMdto(adm);
	}

	@Override
	@BypassInactiveModule
	public void processaAghUnfSeqInAfaDispMdto(AfaDispensacaoMdtos adm) throws ApplicationBusinessException {
		getRealizarTriagemMedicamentosPrescricaoON().processaAghUnfSeqInAfaDispMdto(adm);
	}

	@Override
	@BypassInactiveModule
	public void processaImagensSituacoesDispensacao(AfaDispensacaoMdtos adm) {
		getRealizarTriagemMedicamentosPrescricaoON().processaImagensSituacoesDispensacao(adm);
	}
	
	@Override
	@BypassInactiveModule
	public Integer processaProximaPrescricaoTriagemMedicamentoByProntuario(
			Integer atdSeqPrescricao, Integer seqPrescricao, Boolean proximoRegistro) {
		return getRealizarTriagemMedicamentosPrescricaoON().processaProximaPrescricaoTriagemMedicamentoByProntuario(atdSeqPrescricao, seqPrescricao, proximoRegistro);
	}

	@Override
	@BypassInactiveModule
	public List<MpmPrescricaoMedica> pesquisarAlterarDispensacaoDeMedicamentos(
			Integer firstResult, Integer maxResult, String orderProperty,
			Boolean asc, AinLeitos leito, Integer numeroPrescricao,
			Date dthrDataInicioValidade, Date dthrDataFimValidade,
			Integer numeroProntuario, AipPacientes paciente) {
		return getAlterarDispensacaoDeMedicamentosON()
				.pesquisarAlterarDispensacaoDeMedicamentos(firstResult,
						maxResult, orderProperty, asc, leito, numeroPrescricao,
						dthrDataInicioValidade, dthrDataFimValidade,
						numeroProntuario, paciente);
	}
	
	private AlterarDispensacaoDeMedicamentosON getAlterarDispensacaoDeMedicamentosON(){
		return alterarDispensacaoDeMedicamentosON;
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarAlterarDispensacaoDeMedicamentosCount(
			AinLeitos leito, Integer numeroPrescricao,
			Date dthrDataInicioValidade, Date dthrDataFimValidade,
			Integer numeroProntuario, AipPacientes paciente) {
		return getAlterarDispensacaoDeMedicamentosON()
		.pesquisarAlterarDispensacaoDeMedicamentosCount(leito,
				numeroPrescricao, dthrDataInicioValidade,
				dthrDataFimValidade, numeroProntuario, paciente);
	}

	@Override
	@BypassInactiveModule
	public void processaCorSinaleiroPosAtualizacao(
			List<AfaDispensacaoMdtos> listaOcorrenciasMdtosDispensados) {
		getTratarOcorrenciasON().processaCorSinaleiroPosAtualizacao(listaOcorrenciasMdtosDispensados);
	}

	@Override
	@BypassInactiveModule
	public AipPacientes obterPacienteComAtendimentoPorProntuarioOUCodigo(
			Integer numeroProntuario, Integer codigoPaciente) throws ApplicationBusinessException {
		return getEstornaMedicamentoDispensadoON().obterPacienteComAtendimentoPorProntuarioOUCodigo(numeroProntuario, codigoPaciente);
	}
	
	@Override
	@BypassInactiveModule
	public void processaMdtoSelecaoInMovTriagemDisp(AfaDispensacaoMdtos adm,
			List<AfaMedicamento> medicamentos) throws ApplicationBusinessException {
		getMovimentacaoTriagemDispensacaoMdtosON().processaMdtoSelecaoInMovTriagemDisp(adm, medicamentos);
		
	}

	@Override
	@BypassInactiveModule
	public void processarQtdeMaterialDisponivelEstoque(AfaDispensacaoMdtos adm) {
		getRealizarTriagemMedicamentosPrescricaoON().processarSinalizadorSaldoInsuficiente(adm);
		
	}

	@Override
	@BypassInactiveModule
	public String validaSeMedicamentoVencidoByEtiqueta(String etiqueta) throws ApplicationBusinessException {
		return getEstornarMedicamentoDispensadoON().validaSeMedicamentoVencidoByEtiqueta(etiqueta);
	}

	@Override
	@BypassInactiveModule
	public void validaSeMicroComputadorDispensador(AghUnidadesFuncionais unidadeFuncional, AfaDispensacaoMdtos admNew, AghMicrocomputador microUserDispensador) throws ApplicationBusinessException {
		getTratarOcorrenciasON().validaSeMicroComputadorDispensador(unidadeFuncional, admNew, microUserDispensador);
	}

	@Override
	@BypassInactiveModule
	public Object[] obterNomeUnidFuncComputadorDispMdtoCb(AghUnidadesFuncionais unidadeFuncionalMicro, String nomeComputador) throws ApplicationBusinessException {
		return getDispensacaoMdtosCodBarrasON().obterNomeUnidFuncComputadorDispMdtoCb(unidadeFuncionalMicro, nomeComputador); 
	}

	// # 15278 - RELATÓRIO LISTA OCORRENCIA
	@Override
	@BypassInactiveModule
	public List<ListaOcorrenciaVO> recuperarRelatorioListaOcorrencia(String unidade, String dtReferencia, String ocorrencia, String unidFarmacia, Boolean unidPsiquiatrica)throws ApplicationBusinessException{
		return getTratarOcorrenciasON().recuperarRelatorioListaOcorrencia(unidade, dtReferencia, ocorrencia, unidFarmacia, unidPsiquiatrica);
	}
	
	@Override
	@BypassInactiveModule
	public void recuperarRelatorioListaOcorrenciaCount(String unidade, String dtReferencia, String ocorrencia, String unidFarmacia)throws ApplicationBusinessException {
		getTratarOcorrenciasON().recuperarRelatorioListaOcorrenciaCount(unidade, dtReferencia, ocorrencia, unidFarmacia);
	}

	@Override
	@BypassInactiveModule
	public Boolean verificarAcessoOcorrenciaDeTriagem(String nomeComputador) {
		return getRealizarTriagemMedicamentosPrescricaoON().verificarAcessoOcorrenciaDeTriagem(nomeComputador);
	}

	@Override
	@BypassInactiveModule
	public AghUnidadesFuncionais getFarmaciaMicroComputador(
			AghMicrocomputador micro, String computadorNomeRede) throws ApplicationBusinessException {
		return getTratarOcorrenciasON().getFarmaciaMicroComputador(micro, computadorNomeRede);
	}
	
	protected EstornaMedicamentoDispensadoRN getEstornaMedicamentoDispensadoRN() {
		return estornaMedicamentoDispensadoRN;
	}
	
	@Override
	@BypassInactiveModule
	public DominioSituacaoDispensacaoMdto[] pesquisarFiltroDispensacaoMdtos() {
		return getPesquisarPacientesParaDispensacaoON().pesquisarFiltroDispensacaoMdtos();
	}
	
	private AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO(){
		return afaDispensacaoMdtosDAO;
	}
	
	public AfaPrescricaoMedicamentoDAO getAfaPrescricaoMedicamentoDAO() {
		return afaPrescricaoMedicamentoDAO;
	}

	@Override
	@BypassInactiveModule
	public List<AfaDispensacaoMdtoVO> obterDispensacaoMdtosPorAtendimentoEDataCriacaoEntreDataIntEDataAlta(Integer atdSeq, Date dataInt, Date dataAlta) {
		return getAfaDispensacaoMdtosDAO().obterDispensacaoMdtosPorAtendimentoEDataCriacaoEntreDataIntEDataAlta(atdSeq, dataInt, dataAlta);
	}
	
	@Override
	@BypassInactiveModule
	public void refresh(List<AfaDispensacaoMdtos> listaDispensacao) {
				getDispensacaoMdtosCodBarrasON().refresh(listaDispensacao);
	}
	
	@Override
	@BypassInactiveModule
	public Boolean verificarPermissaoDispensacaoMdtoSemEtiqueta(AfaDispensacaoMdtos dispMdto) {
		return getTratarOcorrenciasON().verificarPermissaoDispensacaoSemEtiqueta(dispMdto);
	}
	
	@Override
	@BypassInactiveModule
	public Long pesquisarDispensacaoMdtosCount(
			MpmPrescricaoMedica prescricaoMedica, Short unfSeq,
			Long seqPrescricaoNaoEletronica) {
		return getMedicamentosDispensacaoON().pesquisarDispensacaoMdtosCount(prescricaoMedica, unfSeq, seqPrescricaoNaoEletronica);
	}
	
	@Override
	@BypassInactiveModule
	public List<AfaPrescricaoMedicamento> pesquisarPrescricaoMedicamentos(Integer prontuario, Date dtReferenciaMinima) {
		return getAfaPrescricaoMedicamentoDAO().pesquisarPrescricaoMedicamentos(prontuario, dtReferenciaMinima);
	}
	
	@Override
	@BypassInactiveModule
	public List<AfaDispensacaoMdtos> pesquisarDispensacaoMdtoPorPrescricaoNaoEletronica(Long seqAfaPrescricaoMedicamento, Integer matCodigo) {
		return dispensacaoDePrescricaoNaoEletronicaON.pesquisarDispensacaoMdtoPorPrescricaoNaoEletronica(seqAfaPrescricaoMedicamento, matCodigo);
	}
	
	@Override
	public void persistirAfaPrescricaoMedicamento(AfaPrescricaoMedicamento prescricaoMedicamento) throws ApplicationBusinessException{
		dispensacaoDePrescricaoNaoEletronicaON.persistirAfaPrescricaoMedicamento(prescricaoMedicamento);
	}
	
	@Override
	public void persistirAfaDispMdtoComPrescricaoNaoEletronica(
			Integer medMatCodigo, BigDecimal qtdeDispensada,
			String observacao, Long seqAfaPrescricaoMedicamento,
			Integer atdSeq, String nomeMicrocomputador, RapServidores servidorLogado,
			Short unfSeq, Short unfSeqSolicitante) throws BaseException{
		dispensacaoDePrescricaoNaoEletronicaON
				.persistirAfaDispMdtoComPrescricaoNaoEletronica(medMatCodigo,
						qtdeDispensada, observacao,
						seqAfaPrescricaoMedicamento, atdSeq,
						nomeMicrocomputador, servidorLogado, unfSeq, unfSeqSolicitante);
	}

	@Override
	public void atualizarAfaDispMdtoComPrescricaoNaoEletronica(AfaDispensacaoMdtos afaDispOld,
			BigDecimal qtdeDispensada, String observacao,
			String nomeMicrocomputador, RapServidores servidorLogado, Boolean edicaoDeDispensacaoComCB) throws BaseException{
		dispensacaoDePrescricaoNaoEletronicaON.
			atualizarAfaDispMdtoComPrescricaoNaoEletronica(afaDispOld, qtdeDispensada, observacao, nomeMicrocomputador, servidorLogado, edicaoDeDispensacaoComCB);
	}

	@Override
	public void dispensaMdtoComCBPrescricaoNaoEletronica(String nroEtiqueta,
			Integer atdSeq, String nomeMicrocomputador,
			RapServidores servidorLogado, Short unfSeq,
			Short unfSeqSolicitante, Long seqAfaPrescricaoMedicamento)
			throws BaseException {
		dispensacaoDePrescricaoNaoEletronicaON
				.dispensaMdtoComCBPrescricaoNaoEletronica(nroEtiqueta,
						atdSeq, nomeMicrocomputador, servidorLogado, unfSeq,
						unfSeqSolicitante, seqAfaPrescricaoMedicamento);
	}
	
	@Override
	public void efetuarEstornoDispensacaoMdtoNaoEletronica(
			AfaDispensacaoMdtos afaDispensacao, String nomeComputadorRede, RapServidores usuarioLogado)
					throws BaseException {
		dispensacaoDePrescricaoNaoEletronicaON.efetuarEstornoDispensacaoMdtoNaoEletronica(afaDispensacao, nomeComputadorRede, usuarioLogado);
	}
	
	@Override
	@BypassInactiveModule
	public String gerarTicketDispensacaoMdto(Integer prontuario, 
			String local, String paciente, 
			String prescricao_inicio, 
			String prescricao_fim,
			Boolean dispencacaoComMdto, Boolean prescricaoEletronica,
			List<TicketMdtoDispensadoVO> listaMdto,
			String relatorioEmitidoPor) throws ApplicationBusinessException{
		
		return getImprimirTicketDispensacaoMedicamentoON().gerarTicketDispensacaoMdto(
				prontuario, local, paciente, prescricao_inicio, prescricao_fim,
				dispencacaoComMdto, prescricaoEletronica,
				listaMdto, relatorioEmitidoPor);
	}	
	
	@Override
	public void atualizarRegistroImpressao(
			List<TicketMdtoDispensadoVO> listaMdtoDispensado,
			RapServidores servidorLogado) {
		getImprimirTicketDispensacaoMedicamentoON().atualizarRegistroImpressao(listaMdtoDispensado, servidorLogado);
	}
	
	@Override
	@BypassInactiveModule
	public Date pesquisarMaxDataHrTicket(Integer atdSeqPrescricao,
			Integer seqPrescricao, AghUnidadesFuncionais unidadeFuncionalMicro,
			Long pmmSeq) {
		return getDispensacaoMdtosCodBarrasON().pesquisarMaxDataHrTicket(atdSeqPrescricao, seqPrescricao, unidadeFuncionalMicro, pmmSeq);
	}
	
	@Override
	@BypassInactiveModule
	public List<TicketMdtoDispensadoVO> pesquisarDispensacaoMdto(
			Integer atdSeqPrescricao, Integer seqPrescricao,
			AghUnidadesFuncionais unidadeFuncionalMicro, Long pmmSeq) {
		return getDispensacaoMdtosCodBarrasON().pesquisarDispensacaoMdto(atdSeqPrescricao, seqPrescricao, unidadeFuncionalMicro, pmmSeq);
	}
	
	@Override
	@BypassInactiveModule
	public Date recuperarDataLimite() throws ApplicationBusinessException {
		return getEstornarMedicamentoDispensadoON().recuperarDataLimite();
	}
	
	@Override
	@BypassInactiveModule
	public ConsultaDispensacaoMedicamentosVO preencherVoDispensacaoMedicamentos(
			MpmPrescricaoMedica prescricaoMedica, Long seqPrescricaoNaoEletronica) {
		return getMedicamentosDispensacaoON().pesquisarDispensacaoMdtosCount(prescricaoMedica, seqPrescricaoNaoEletronica);
	}
}