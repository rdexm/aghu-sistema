package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.dao.PdtAchadoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtAvalPreSedacaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtCidDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtCidPorProcDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtComplementoPorCidDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDadoDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDadoImgDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescObjetivaDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescPadraoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescTecnicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtEquipPorProcDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtEquipamentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtGrupoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtImgDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtInstrDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtMedicDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtMedicUsualDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtNotaAdicionalDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtNotaAdicionalJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtProcDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtProcDiagTerapDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtProfDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtSolicTempDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtTecnicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtTecnicaPorProcDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtTecnicaPorProcJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtViaAereasDAO;
import br.gov.mec.aghu.blococirurgico.vo.CertificarRelatorioCirurgiasPdtVO;
import br.gov.mec.aghu.blococirurgico.vo.ComplementoCidVO;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoProcDiagTerapVO;
import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricao;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.PdtAchado;
import br.gov.mec.aghu.model.PdtAvalPreSedacao;
import br.gov.mec.aghu.model.PdtCidDesc;
import br.gov.mec.aghu.model.PdtCidDescId;
import br.gov.mec.aghu.model.PdtCidPorProc;
import br.gov.mec.aghu.model.PdtCidPorProcId;
import br.gov.mec.aghu.model.PdtComplementoPorCid;
import br.gov.mec.aghu.model.PdtDadoDesc;
import br.gov.mec.aghu.model.PdtDadoImg;
import br.gov.mec.aghu.model.PdtDescObjetiva;
import br.gov.mec.aghu.model.PdtDescPadrao;
import br.gov.mec.aghu.model.PdtDescTecnica;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.PdtDescricao.Fields;
import br.gov.mec.aghu.model.PdtEquipPorProc;
import br.gov.mec.aghu.model.PdtEquipamento;
import br.gov.mec.aghu.model.PdtGrupo;
import br.gov.mec.aghu.model.PdtGrupoId;
import br.gov.mec.aghu.model.PdtImg;
import br.gov.mec.aghu.model.PdtInstrDesc;
import br.gov.mec.aghu.model.PdtInstrDescId;
import br.gov.mec.aghu.model.PdtMedicDesc;
import br.gov.mec.aghu.model.PdtMedicDescId;
import br.gov.mec.aghu.model.PdtMedicUsual;
import br.gov.mec.aghu.model.PdtNotaAdicional;
import br.gov.mec.aghu.model.PdtProc;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.model.PdtProcId;
import br.gov.mec.aghu.model.PdtProf;
import br.gov.mec.aghu.model.PdtProfId;
import br.gov.mec.aghu.model.PdtSolicTemp;
import br.gov.mec.aghu.model.PdtTecnica;
import br.gov.mec.aghu.model.PdtTecnicaPorProc;
import br.gov.mec.aghu.model.PdtViaAereas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProcedimentosPOLVO;


@Modulo(ModuloEnum.BLOCO_CIRURGICO)
@SuppressWarnings({"PMD.CouplingBetweenObjects","PMD.ExcessiveClassLength" })
@Stateless
public class BlocoCirurgicoProcDiagTerapFacade extends BaseFacade implements IBlocoCirurgicoProcDiagTerapFacade {

	@Inject
	private PdtNotaAdicionalJnDAO pdtNotaAdicionalJnDAO;

	@Inject
	private PdtSolicTempDAO pdtSolicTempDAO;

	@Inject
	private PdtAvalPreSedacaoDAO pdtAvalPreSedacaoDAO;

	@Inject
	private PdtDescObjetivaDAO pdtDescObjetivaDAO;

	@Inject
	private PdtTecnicaPorProcJnDAO pdtTecnicaPorProcJnDAO;

	@Inject
	private PdtProcDiagTerapDAO pdtProcDiagTerapDAO;

	@Inject
	private PdtGrupoDAO pdtGrupoDAO;

	@Inject
	private PdtProcDAO pdtProcDAO;

	@Inject
	private PdtProfDAO pdtProfDAO;

	@Inject
	private PdtDescTecnicaDAO pdtDescTecnicaDAO;

	@Inject
	private PdtDescPadraoDAO pdtDescPadraoDAO;

	@Inject
	private PdtDescricaoDAO pdtDescricaoDAO;

	@Inject
	private PdtMedicDescDAO pdtMedicDescDAO;

	@Inject
	private PdtMedicUsualDAO pdtMedicUsualDAO;

	@Inject
	private PdtCidPorProcDAO pdtCidPorProcDAO;

	@Inject
	private PdtEquipamentoDAO pdtEquipamentoDAO;

	@Inject
	private PdtDadoImgDAO pdtDadoImgDAO;

	@Inject
	private PdtTecnicaPorProcDAO pdtTecnicaPorProcDAO;

	@Inject
	private PdtDadoDescDAO pdtDadoDescDAO;

	@Inject
	private PdtTecnicaDAO pdtTecnicaDAO;

	@Inject
	private PdtComplementoPorCidDAO pdtComplementoPorCidDAO;

	@Inject
	private PdtImgDAO pdtImgDAO;

	@Inject
	private PdtEquipPorProcDAO pdtEquipPorProcDAO;

	@Inject
	private PdtAchadoDAO pdtAchadoDAO;

	@Inject
	private PdtInstrDescDAO pdtInstrDescDAO;

	@Inject
	private PdtCidDescDAO pdtCidDescDAO;

	@Inject
	private PdtNotaAdicionalDAO pdtNotaAdicionalDAO;

	@Inject
	private PdtViaAereasDAO pdtViaAereasDAO;


	@EJB
	private PdtNotaAdicionalRN pdtNotaAdicionalRN;

	@EJB
	private GruposAchadosON gruposAchadosON;

	@EJB
	private PdtTecnicaPorProcON pdtTecnicaPorProcON;

	@EJB
	private PdtProcRN pdtProcRN;

	@EJB
	private PdtDescTecnicaRN pdtDescTecnicaRN;

	@EJB
	private PdtMedicDescRN pdtMedicDescRN;

	@EJB
	private PdtAvalPreSedacaoRN pdtAvalPreSedacaoRN;

	@EJB
	private DescricaoProcDiagTerapProcedimentoON descricaoProcDiagTerapProcedimentoON;

	@EJB
	private PdtCidPorProcON pdtCidPorProcON;

	@EJB
	private PdtSolicTempRN pdtSolicTempRN;

	@EJB
	private PdtTecnicaON pdtTecnicaON;

	@EJB
	private LiberacaoLaudoPreliminarON liberacaoLaudoPreliminarON;

	@EJB
	private CancelamentoDescricaoCirurgicaPdtRN cancelamentoDescricaoCirurgicaPdtRN;

	@EJB
	private PdtMedicUsualON pdtMedicUsualON;

	@EJB
	private DescricaoProcDiagTerapRN descricaoProcDiagTerapRN;

	@EJB
	private PdtDadoDescRN pdtDadoDescRN;

	@EJB
	private PdtDescricaoRN pdtDescricaoRN;

	@EJB
	private PdtCidDescON pdtCidDescON;

	@EJB
	private PdtDescObjetivaRN pdtDescObjetivaRN;

	@EJB
	private PdtCidDescRN pdtCidDescRN;

	@EJB
	private PdtProcDiagTerapRN pdtProcDiagTerapRN;

	@EJB
	private DescricaoProcDiagTerapEquipeON descricaoProcDiagTerapEquipeON;

	@EJB
	private PdtProfRN pdtProfRN;

	@EJB
	private PdtInstrDescRN pdtInstrDescRN;

	private static final long serialVersionUID = -7033519707935762440L; 

	@Override
	public PdtDescricao carregarDescricaoProcDiagTerap(Integer crgSeq) throws ApplicationBusinessException {
		return getDescricaoProcDiagTerapRN().carregarDescricaoProcDiagTerap(crgSeq);
	} 

	@Override
	public void desfazCarregamentoDescricaoCirurgicaPDT(final Integer crgSeq, final Integer ddtSeq) throws ApplicationBusinessException, ApplicationBusinessException{
		getCancelamentoDescricaoCirurgicaPdtRN().desfazCarregamentoDescricaoCirurgicaPDT(crgSeq, ddtSeq);
	}

	@Override
	public boolean habilitarNotasAdicionais(final Integer crgSeq, final PdtDescricao descricao) throws ApplicationBusinessException {
		return getDescricaoProcDiagTerapRN().habilitarNotasAdicionais(crgSeq, descricao);
	}
	
	@Override
	public void inserirProf(Integer ddtSeq, ProfDescricaoCirurgicaVO profDescricaoCirurgicaVO
			) throws ApplicationBusinessException {
		getDescricaoProcDiagTerapEquipeON().inserirProf(ddtSeq, profDescricaoCirurgicaVO);
	}
	
	@Override
	public void removerProf(PdtProf oldProf) {
		getPdtProfRN().removerProf(oldProf);
	}
	
	@Override
	public PdtProf obterPdtProfPorChavePrimaria(PdtProfId pdtProfId){
		return getPdtProfDAO().obterPorChavePrimaria(pdtProfId);
	}
	
	protected DescricaoProcDiagTerapRN getDescricaoProcDiagTerapRN() {
		return descricaoProcDiagTerapRN;
	}
	
	@Override
	public List<PdtNotaAdicional> pesquisarNotaAdicionalPorDdtSeq(Integer ddtSeq){
		return getPdtNotaAdicionalDAO().pesquisarNotaAdicionalPorDdtSeq(ddtSeq);
	}

	@Override
	public PdtNotaAdicional obterPdtNotaAdicionalPorServidorEPdtDescricao(RapServidores servidor, Integer ddtSeq){
		return getPdtNotaAdicionalDAO().obterPdtNotaAdicionalPorServidorEPdtDescricao(servidor, ddtSeq);
	}

	@Override
	public List<PdtNotaAdicional> pesquisarNotaAdicionalPorDdtSeqESeqpDesc(Integer seqPdtDescricao) {
		return getPdtNotaAdicionalDAO().pesquisarNotaAdicionalPorDdtSeqESeqpDesc(seqPdtDescricao);
	}

	@Override
	public void persistirPdtNotaAdicional(final PdtNotaAdicional notaAdicional) throws ApplicationBusinessException{
		getPdtNotaAdicionalRN().persistirPdtNotaAdicional(notaAdicional);
	}

	@Override
	public void excluirPdtNotaAdicional(final PdtNotaAdicional notaAdicional) throws ApplicationBusinessException{
		getPdtNotaAdicionalRN().excluirPdtNotaAdicional(notaAdicional);
	}

	@Override
	public List<PdtDescObjetiva> pesquisarPdtDescObjetivaPorDdtSeq(Integer seq) {
		return getPdtDescObjetivaDAO().pesquisarPdtDescObjetivaPorDdtSeq(seq);
	}
	
	@Override
	public List<PdtDadoImg> pesquisarPdtDadoImgPorDdtSeq(Integer ddtSeq){
		return getPdtDadoImgDAO().pesquisarPdtDadoImgPorDdtSeq(ddtSeq);
	}

	@Override
	public List<PdtImg> pesquisarImagens(Integer cirSeq) {
		return this.getPdtImgDAO().pesquisarImagens(cirSeq);
	}

	@Override
	public Long verificarSeTemImagem(Integer ddtSeq) {
		return this.getPdtImgDAO().verificarSeTemImagem(ddtSeq);
	}

	@Override
	public PdtImg obterPdtImgPorDdtSeqESeq(Short seqp, Integer ddtSeq) {
		return getPdtImgDAO().obterPdtImgPorChavePrimaria(seqp, ddtSeq );
	}
	

	@Override
	public Long pesquisarDescricaoPadraoCount(Short especialidadeId, Integer procedimentoId, String titulo) {
		return this.getPdtDescPadraoDAO().pesquisarDescricaoPadraoCount(especialidadeId, procedimentoId, titulo);
	}

	@Override
	public List<PdtDescPadrao> pesquisarDescricaoPadrao(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Short especialidadeId, Integer procedimentoId, String titulo) {
		return this.getPdtDescPadraoDAO().pesquisarDescricaoPadrao(firstResult,
				maxResult, orderProperty, asc,
				especialidadeId, procedimentoId, titulo);
	}

	@Override
	public PdtDescPadrao obterDescricaoPadraoById(Short seqp, Short espSeq) {
		return getPdtDescPadraoDAO().obterDescricaoPadraoById(seqp, espSeq);
	}
	
	@Override
	public List<PdtDescPadrao> pesquisarDescPadraoProcedimentoCirurgicoAtivoPorEspSeqEPciSeq(Short espSeq, Integer pciSeq) {
		return getPdtDescPadraoDAO().pesquisarDescPadraoProcedimentoCirurgicoAtivoPorEspSeqEPciSeq(espSeq, pciSeq);
	}

	@Override
	public PdtDescTecnica obterDescricaoTecnicaPorDdtSeq(Integer ddtSeq){
		return getPdtDescTecnicaDAO().obterDescricaoTecnicaPorDdtSeq(ddtSeq);
	}
	
	@Override
	public PdtDescTecnica obterDescricaoTecnicaPorChavePrimaria(Integer seq) {
		return getPdtDescTecnicaDAO().obterPorChavePrimaria(seq);
	}
	
	@Override
	public void persistirPdtDescTecnica(final PdtDescTecnica descricaoTecnica) throws ApplicationBusinessException {
		getPdtDescTecnicaRN().persistirPdtDescTecnica(descricaoTecnica);
	}
	
	@Override
	public void excluirPdtDescTecnica(final PdtDescTecnica descricaoTecnica) throws ApplicationBusinessException {
		getPdtDescTecnicaRN().excluir(descricaoTecnica);		
	}
	
	protected PdtDescTecnicaRN getPdtDescTecnicaRN() {
		return pdtDescTecnicaRN;
	}
	
	@Override
	public PdtDadoDesc obterDadoDescPorDdtSeq(Integer seq) {
		return getPdtDadoDescDAO().obterDadoDescPorDdtSeq(seq);
	}
	
	@Override
	public PdtDadoDesc obterDadoDescPorChavePrimaria(Integer seq) {
		return getPdtDadoDescDAO().obterPorChavePrimaria(seq, true, PdtDadoDesc.Fields.PDT_TECNICAS, PdtDadoDesc.Fields.PDT_EQUIPAMENTOS, PdtDadoDesc.Fields.MBC_TIPO_ANESTESIAS);
	}
	
	@Override
	public void inserirDadoDesc(PdtDadoDesc newDadoDesc) 
			throws ApplicationBusinessException {
		getPdtDadoDescRN().inserirDadoDesc(newDadoDesc);
	}
	
	@Override
	public PdtDadoDesc atualizarDadoDesc(PdtDadoDesc newDadoDesc) throws ApplicationBusinessException {
		return getPdtDadoDescRN().atualizarDadoDesc(newDadoDesc);
	}
	
	protected PdtDadoDescRN getPdtDadoDescRN() {
		return pdtDadoDescRN;
	}
	
	@Override
	public RapServidores buscaRapServidorDePdtProfissao(Integer crgSeq, DominioTipoAtuacao tipoAtuacao){
		return getPdtProfDAO().buscaRapServidor(crgSeq, tipoAtuacao);
	}	

	@Override
	public List<PdtProf> pesquisarProfPorDdtSeq(Integer ddtSeq) {
		return getPdtProfDAO().pesquisarProfPorDdtSeq(ddtSeq);
	}

	@Override
	public List<PdtProf> buscaPdtProfissaoPorDdtSeqETipoAtuacao(Integer ddtSeq, DominioTipoAtuacao tipoAtuacao, Integer serMatricula){
		return getPdtProfDAO().buscaPdtProfissaoPorDdtSeqETipoAtuacao(ddtSeq, tipoAtuacao, serMatricula);
	}
	
	@Override
	public List<PdtProf> pesquisarProfPorDdtSeqETipoAtuacao(Integer ddtSeq, DominioTipoAtuacao tipoAtuacao) {
		return getPdtProfDAO().pesquisarProfPorDdtSeqETipoAtuacao(ddtSeq, tipoAtuacao);
	}
	
	@Override
	public List<PdtProf> pesquisarProfPorDdtSeqEListaTipoAtuacao(Integer ddtSeq, List<DominioTipoAtuacao> listaTipoAtuacao) {
		return getPdtProfDAO().pesquisarProfPorDdtSeqEListaTipoAtuacao(ddtSeq, listaTipoAtuacao);
	}
	
	@Override
	public List<PdtProf> pesquisarProfPorDdtSeqServidorETipoAtuacao(Integer ddtSeq, Integer serMatricula, Short serVinCodigo, DominioTipoAtuacao tipoAtuacao) {
		return getPdtProfDAO().pesquisarProfPorDdtSeqServidorETipoAtuacao(ddtSeq, serMatricula, serVinCodigo, tipoAtuacao);
	}
	
	@Override
	public Short obterMaiorSeqpProfPorDdtSeq(Integer ddtSeq) {
		return getPdtProfDAO().obterMaiorSeqpProfPorDdtSeq(ddtSeq);
	}

	@Override
	public String obterNomePessoaPdtProfByPdtDescricao(Integer seqPdtDescricao,
			Integer seqMbcCirurgia) {
		return getPdtProfDAO().obterNomePessoaPdtProfByPdtDescricao(seqPdtDescricao, seqMbcCirurgia);
	}
	
	@Override
	public List<PdtProcDiagTerap> listarProcDiagTerap(Object strPesquisa) {
		return getPdtProcDiagTerapDAO().listarProcDiagTerap(strPesquisa);
	}

	@Override
	public Long listarProcDiagTerapCount(Object strPesquisa) {
		return getPdtProcDiagTerapDAO().listarProcDiagTerapCount(strPesquisa);
	}

	@Override
	public Long pesquisarPdtProcDiagTerapCount(String strPesquisa) {
		return getPdtProcDiagTerapDAO().pesquisarPdtGrupoPorIdDescricaoSituacaoCount(strPesquisa);
	}

	@Override
	public List<PdtProcDiagTerap> pesquisarPdtProcDiagTerap(String strPesquisa) {
		return getPdtProcDiagTerapDAO().pesquisarPdtProcDiagTerap(strPesquisa);
	}

	@Override
	public PdtProcDiagTerap obterPdtProcDiagTerapPorChavePrimaria(Integer seq) {
		return getPdtProcDiagTerapDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public PdtProcDiagTerap obterPdtProcDiagTerap( Integer seqPdtProcDiagTerap) {
		return getPdtProcDiagTerapDAO().obterPorChavePrimaria(seqPdtProcDiagTerap);
	}
	
	@Override
	public void inserirProcDiagTerap(PdtProcDiagTerap newProcDiagTerap) {
		getProcDiagTerapRN().inserirProcDiagTerap(newProcDiagTerap);
	}
	
	@Override
	public void atualizarProcDiagTerap(PdtProcDiagTerap newProcDiagTerap) {
		getProcDiagTerapRN().atualizarProcDiagTerap(newProcDiagTerap);
	}

	@Override
	public List<PdtProcDiagTerap> listarProcDiagTerapAtivaPorDescricao(Object strPesquisa) {
		return getPdtProcDiagTerapDAO().listarProcDiagTerapAtivaPorDescricao(strPesquisa);
	}
	
	@Override
	public Long listarProcDiagTerapAtivaPorDescricaoCount(Object strPesquisa) {
		return getPdtProcDiagTerapDAO().listarProcDiagTerapAtivaPorDescricaoCount(strPesquisa);
	}
	
	@Override
	public List<AghEspecialidades> pesquisarEspecialidadeDescricaoPadraoProcCirurgicoAtivo() {
		return getPdtDescPadraoDAO().pesquisarEspecialidadeDescricaoPadraoProcCirurgicoAtivo();
	}

	@Override
	public List<MbcProcedimentoCirurgicos> pesquisarProcCirurgicoAtivoDescricaoPadraoPorEspSeq(Short espSeq) {
		return getPdtDescPadraoDAO().pesquisarProcCirurgicoAtivoDescricaoPadraoPorEspSeq(espSeq);
	}

	@Override
	public List<PdtProc> pesquisarPdtProcPorDdtSeq(Integer seq) {
		return getPdtProcDAO().pesquisarPdtProcPorDdtSeq(seq);
	}

	@Override
	public List<PdtProc> pesquisarPdtProcPorDdtSeqOrdenadoPorSeqP(Integer seq){
		return getPdtProcDAO().pesquisarPdtProcPorDdtSeqOrdenadoPorSeqP(seq);
	}
	
	
	@Override
	public List<PdtProc> pesquisarProcComProcedimentoCirurgicoAtivoPorDdtSeq(Integer ddtSeq) {
		return getPdtProcDAO().pesquisarProcComProcedimentoCirurgicoAtivoPorDdtSeq(ddtSeq);
	}
	
	@Override
	public PdtProc obterPdtProcPorChavePrimaria(PdtProcId id) {
		return getPdtProcDAO().obterPorChavePrimaria(id, null, new Enum[] {PdtProc.Fields.PDT_PROC_DIAG_TERAPS});
	}

	@Override
	public void excluirPdtProc(final PdtProc pdtProc){
		getPdtProcRN().removerProc(pdtProc);
	}

	@Override
	public void persistirPdtProc(final PdtProc pdtProc)throws ApplicationBusinessException {
		getPdtProcRN().persistirPdtProc(pdtProc);
	}
	
	@Override
	public void persistirPdtAvalPreSedacao(final PdtAvalPreSedacao pdtAvalPreSedacao)throws ApplicationBusinessException {
		getPdtAvalPreSedacaoRN().persistirPdtAvalPreSedacao(pdtAvalPreSedacao);
	}
	
	
	@Override
	public PdtAvalPreSedacao pesquisarPdtAvalPreSedacaoPorDdtSeq(Integer seq){
		return getPdtAvalPreSedacaoDAO().pesquisarPdtAvalPreSedacaoPorDdtSeq(seq);
	}

	@Override
	public List<PdtViaAereas> obterViasAereas(){
		return getPdtViaAereasDAO().obterPdtViaAereasAtivasOrdenadas();
	}
	
	
	@Override
	public PdtEquipamento obterEquipamentoPorChavePrimaria(Short seq) {
		return getPdtEquipamentoDAO().obterPorChavePrimaria(seq);
	}
	
	@Override
	public List<PdtEquipamento> pesquisarEquipamentosDiagnosticoTerapeutico(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String descricao, Short codigo,
			DominioSituacao situacao, String enderecoImagens) {
			return this.getPdtEquipamentoDAO().pesquisarEquipamentosDiagnosticoTerapeutico(
					firstResult, maxResult, orderProperty, asc, descricao, codigo, situacao, enderecoImagens);
	}

	@Override
	public Long pesquisarEquipamentosDiagnosticoTerapeuticoCount(
			String descricao, Short codigo, DominioSituacao situacao,
			String enderecoImagens) {
		return this.getPdtEquipamentoDAO().pesquisarEquipamentosDiagnosticoTerapeuticoCount(
		descricao, codigo, situacao, enderecoImagens);
	}

	@Override
	public PdtEquipamento obterPdtEquipamentoPorSeq(Short seq) {
		return getPdtEquipamentoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public List<PdtEquipamento> pesquisarEquipamentoPorNome(Object strPesquisa) {
		return getPdtEquipamentoDAO().pesquisarEquipamentoPorNome(strPesquisa);
	}

	@Override
	public List<PdtEquipamento> listarPdtEquipamentoAtivoPorDescricao(Object strPesquisa){
		return getPdtEquipamentoDAO().listarPdtEquipamentoAtivoPorDescricao(strPesquisa);
	}

	@Override
	public Long listarPdtEquipamentoAtivoPorDescricaoCount(Object strPesquisa){
		return getPdtEquipamentoDAO().listarPdtEquipamentoAtivoPorDescricaoCount(strPesquisa);
	}	

	@Override
	public List<PdtEquipamento> pesquisarEquipamentosDiagnosticoTerapeutico(final Integer dptSeq){
		return getPdtEquipamentoDAO().pesquisarEquipamentosDiagnosticoTerapeutico(dptSeq);
	}
	
	
	@Override
	public Long pesquisarEquipamentoPorNomeCount(Object strPesquisa) {
		return getPdtEquipamentoDAO().pesquisarEquipamentoPorNomeCount(strPesquisa);
	}

	@Override
	public List<PdtEquipPorProc> pesquisarPdtEquipPorProcPorDptSeq(Integer dptSeq) {
		return getPdtEquipPorProcDAO().pesquisarPdtEquipPorProcPorDptSeq(dptSeq);
	}	

	@Override
	public List<PdtEquipPorProc> listarPdtEquipPorProcAtivoPorEquipe(Short seq) {
		return getPdtEquipPorProcDAO().listarPdtEquipPorProcAtivoPorEquipe(seq);
	}
	
	@Override
	public List<PdtTecnicaPorProc> listarPdtTecnicaPorProc(Integer dteSeq) {
		return getPdtTecnicaPorProcDAO().listarPdtTecnicaPorProc(dteSeq);
	}

	@Override
	public List<PdtTecnicaPorProc> pesquisarPdtTecnicaPorProcPorDptSeq(
			Integer dptSeq) {
		return getPdtTecnicaPorProcDAO().pesquisarPdtTecnicaPorProcPorDptSeq(dptSeq);
	}

	@Override
	public PdtTecnicaPorProc obterOriginalPdtTecnicaPorProc(
			PdtTecnicaPorProc tecnicaPorProc) {
		return getPdtTecnicaPorProcDAO().obterOriginal(tecnicaPorProc);
	}


	@Override
	public String persistirPdtTecnicaPorProc(PdtTecnica tecnica,
			PdtProcDiagTerap procDiagTerap) throws ApplicationBusinessException {
		return getPdtTecnicaPorProcON().persistirPdtTecnicaPorProc(tecnica, procDiagTerap);
	}
	
	@Override
	public void refreshPdtTecnicaPorProc(List<PdtTecnicaPorProc> listPdtTecnicasPorProc) {
		getPdtTecnicaPorProcON().refreshPdtTecnicaPorProc(listPdtTecnicasPorProc);
	}
	
	@Override
	public String removerPdtTecnicaPorProc(PdtTecnicaPorProc tecnicaPorProc) {
		return getPdtTecnicaPorProcON().removerPdtTecnicaPorProc(tecnicaPorProc);	
	}
	
	@Override
	public PdtTecnica obterTecnicaPorChavePrimaria(Integer seq) {
		return getPdtTecnicaDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public List<PdtTecnica> pesquisarTecnicaPorDescricaoOuSeq(Object strPesquisa) {
		return getPdtTecnicaDAO().pesquisarTecnicaPorDescricaoOuSeq(strPesquisa);
	}
	
	@Override
	public List<PdtTecnica> listarPdtTecnicaPorSeqDescricaoSituacao(Integer seq, String descricao, DominioSituacao situacao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getPdtTecnicaDAO().listarPdtTecnicaPorSeqDescricaoSituacao(seq, descricao, situacao, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public Long listarPdtTecnicaPorSeqDescricaoSituacaoCount(Integer seq, String descricao, DominioSituacao situacao) {
		return getPdtTecnicaDAO().listarPdtTecnicaPorSeqDescricaoSituacaoCount(seq, descricao, situacao);
	}

	@Override
	public List<PdtTecnica> listarPdtTecnicaPorDptSeq(final Integer dptSeq) {
		return getPdtTecnicaDAO().listarPdtTecnicaPorDptSeq(dptSeq);
	}

	@Override
	public String persistirPdtTecnica(PdtTecnica tecnica) throws ApplicationBusinessException {
		return getPdtTecnicaON().persistirPdtTecnica(tecnica);
	}
	
	@Override
	public List<PdtAchado> pesquisarPdtAchados(Integer dgrDptSeq, Short dgrSeqp) {
		return getPdtAchadoDAO().pesquisarPdtAchados(dgrDptSeq, dgrSeqp);
	}

	@Override
	public void refreshPdtAchado(List<PdtAchado> listaAchados) {
		getGruposAchadosON().refreshPdtAchado(listaAchados);
	}

	@Override
	public String gravarPdtAchado(PdtGrupo grupo, PdtAchado achado) throws ApplicationBusinessException {
		return getGruposAchadosON().gravarPdtAchado(grupo, achado);
	}
	
	@Override
	public String gravarPdtGrupo(PdtGrupo grupo, Integer dptSeq) throws ApplicationBusinessException {
		return getGruposAchadosON().gravarPdtGrupo(grupo, dptSeq);
	}

	@Override
	public PdtGrupo obterPdtGrupoPorId(PdtGrupoId grupoId) {
		return getPdtGrupoDAO().obterPorChavePrimaria(grupoId);
	}
	
	@Override
	public List<PdtGrupo> pesquisarPdtGrupoPorIdDescricaoSituacao( Integer dptSeq, Short seqp, String descricao, DominioSituacao indSituacao,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getPdtGrupoDAO().pesquisarPdtGrupoPorIdDescricaoSituacao(dptSeq, seqp, descricao, indSituacao, 
				firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long pesquisarPdtGrupoPorIdDescricaoSituacaoCount(Integer dptSeq, Short seqp, String descricao, DominioSituacao indSituacao) {
		return getPdtGrupoDAO().pesquisarPdtGrupoPorIdDescricaoSituacaoCount(dptSeq, seqp, descricao, indSituacao);
	}
	
	@Override
	public String excluirPdtMedicUsual(PdtMedicUsual pdtMedicUsualDelecao) throws ApplicationBusinessException {
		return getPdtMedicUsualON().excluirPdtMedicUsual(pdtMedicUsualDelecao);
	}

	@Override
	public String persistirPdtMedicUsual(PdtMedicUsual pdtMedicUsual) throws ApplicationBusinessException {
		return getPdtMedicUsualON().persistirPdtMedicUsual(pdtMedicUsual);
	}

	@Override
	public List<PdtMedicUsual> pesquisaPdtMedicUsual(PdtMedicUsual pdtMedicUsual) {
		return getPdtMedicUsualDAO().pesquisarPdtMedicUsual(pdtMedicUsual);
	}
	

	
	@Override
	public List<PdtCidPorProc> listarPdtCidPorProcPorProcedimentoSituacaoCid(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, PdtProcDiagTerap procedimento, DominioSituacao situacao, AghCid cid) {
		return getPdtCidPorProcDAO().listarPdtCidPorProcPorProcedimentoSituacaoCid(firstResult, maxResult, orderProperty, asc, procedimento, situacao, cid);
	}

	@Override
	public Long listarPdtCidPorProcPorProcedimentoSituacaoCidCount(PdtProcDiagTerap procedimento, DominioSituacao situacao, AghCid cid) {
		return getPdtCidPorProcDAO().listarPdtCidPorProcPorProcedimentoSituacaoCidCount(procedimento, situacao, cid);
	}
	
	@Override
	public PdtCidPorProc obterPdtCidPorProcPorChavePrimaria(Integer dptSeq, Integer cidSeq) {
		return getPdtCidPorProcDAO().obterPorChavePrimaria(new PdtCidPorProcId(dptSeq,cidSeq));
	}

	@Override
	public String persistirCidProcedimento(PdtCidPorProc cidProcedimento,
			Integer dptSeq, Integer cidSeq)
			throws ApplicationBusinessException {
		return getCidsProcedimentoON().persistirCidProcedimento(cidProcedimento, dptSeq, cidSeq);
	}
	
	@Override
	public List<PdtComplementoPorCid> listarPdtComplementoPorCids(Integer dptSeq, Integer cidSeq) {
		return getPdtComplementoPorCidDAO().listarPdtComplementoPorCids(dptSeq,cidSeq);
	}

	@Override
	public String persistirComplemento(PdtComplementoPorCid complemento, Integer dptSeq, Integer cidSeq) throws ApplicationBusinessException {
		return getCidsProcedimentoON().persistirComplemento(complemento, dptSeq, cidSeq);
	}
	
	@Override
	public PdtSolicTemp obterSolicTempPorDdtSeq(Integer seq) {
		return getPdtSolicTempDAO().obterSolicTempPorDdtSeq(seq);
	}

	@Override
	public List<PdtMedicDesc> pesquisarMedicDescPorDdtSeq(Integer seq) {
		return getPdtMedicDescDAO().pesquisarMedicDescPorDdtSeq(seq);
	}

	@Override
	public List<PdtMedicDesc> pesquisarMedicDescPorDdtSeqOrdenadoPorDdtSeqESeqp(Integer seq) {
		return getPdtMedicDescDAO().pesquisarMedicDescPorDdtSeqOrdenadoPorDdtSeqESeqp(seq);
	}

	@Override
	public PdtMedicDesc obterPdtMedicDescPorChavePrimaria(PdtMedicDescId pdtMedicDescId){
		return getPdtMedicDescDAO().obterPorChavePrimaria(pdtMedicDescId, true, PdtMedicDesc.Fields.AFA_MEDICAMENTO);
	}

	@Override
	public void persistirPdtMedicDesc(final PdtMedicDesc medicDescricao) throws ApplicationBusinessException {
		getPdtMedicDescRN().persistirPdtMedicDesc(medicDescricao);
	}

	@Override
	public void excluirPdtMedicDesc(final PdtMedicDesc medicDescricao) throws ApplicationBusinessException{
		getPdtMedicDescRN().excluir(medicDescricao);
	}
	
	@Override
	public List<PdtDescricao> listarDescricaoPorSeqCirurgiaSituacao(Integer crgSeq, DominioSituacaoDescricao[] situacao) {
		return this.getPdtDescricaoDAO().listarDescricaoPorSeqCirurgiaSituacao(crgSeq, situacao);
	}

	@Override
	public List<PdtDescricao> listarDescricaoPorSeqCirurgiaSituacao(
			Integer crgSeq, DominioSituacaoDescricao[] situacao, Fields order) {
		return this.getPdtDescricaoDAO().listarDescricaoPorSeqCirurgiaSituacao(
				crgSeq, situacao, order);
	}

	@Override
	public Long listarDescricaoPorSeqCirurgiaSituacaoCount(
			Integer crgSeq, DominioSituacaoDescricao[] situacao) {
		return this.getPdtDescricaoDAO().listarDescricaoPorSeqCirurgiaSituacaoCount(crgSeq, situacao);
	}

	@Override
	public List<PdtDescricao> listarDescricaoPorSeqCirurgia(Integer seqCirurgia) {
		return this.getPdtDescricaoDAO().listarDescricaoPorSeqCirurgia(
				seqCirurgia);
	}

	@Override
	public PdtDescricao obterPdtDescricao(Integer seqPdtDescricao, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return getPdtDescricaoDAO().obterPorChavePrimaria(seqPdtDescricao,fetchArgsInnerJoin,fetchArgsLeftJoin);
	}
	
	@Override
	public List<PdtDescricao> pesquisarDescricaoPorCirurgiaEServidor(Integer crgSeq, Integer serMatricula, Short serVinCodigo) {
		return getPdtDescricaoDAO().pesquisarDescricaoPorCirurgiaEServidor(crgSeq, serMatricula, serVinCodigo);
	}	
	
	@Override
	public void persistirIndicacao(PdtSolicTemp solicitacao) {
		getDescricaoProcDiagTerapIndicacaoRN().persistir(solicitacao);
	}
	
	@Override
	public void atualizarDescricao(final PdtDescricao newDescricao) throws BaseException {
		getPdtDescricaoRN().atualizarDescricao(newDescricao, false);
	}
	
	@Override
	public void validarContaminacaoProcedimentoCirurgicoPdt(Integer pciSeq, DominioIndContaminacao novoIndContaminacao) throws ApplicationBusinessException {
		getDescricaoProcDiagTerapProcedimentoON().validarContaminacaoProcedimentoCirurgicoPdt(pciSeq, novoIndContaminacao);
	}

	@Override
	public List<PdtInstrDesc> pesquisarPdtInstrDescPorDdtSeq(final Integer ddtSeq) {
		return getPdtInstrDescDAO().pesquisarPdtInstrDescPorDdtSeq(ddtSeq);
	}

	@Override
	public PdtInstrDesc obterPdtInstrDescPorChavePrimaria(PdtInstrDescId pdtInstrDescId){
		return getPdtInstrDescDAO().obterPorChavePrimaria(pdtInstrDescId, true, PdtInstrDesc.Fields.PDT_INSTRUMENTAIS);
	}

	@Override
	public void excluirPdtInstrDesc(PdtInstrDesc instrDesc) throws ApplicationBusinessException{
		getPdtInstrDescRN().excluir(instrDesc);
	}

	@Override
	public void inserirPdtInstrDesc(final PdtInstrDesc instrucaoDescritiva) throws ApplicationBusinessException{
		getPdtInstrDescRN().inserir(instrucaoDescritiva);
	}
	
	@Override
	public List<ComplementoCidVO> obterListaComplementoCid(Integer ddtSeq, Object strPesquisa) {
		return getPdtProcDAO().obterListaComplementoCid(ddtSeq, strPesquisa);
	}

	@Override
	public Long obterListaComplementoCidCount(Integer ddtSeq, Object strPesquisa) {
		return getPdtProcDAO().obterListaComplementoCidCount(ddtSeq, strPesquisa);
	}
	
	@Override
	public Long countListaComplementoCid(Integer ddtSeq, Object strPesquisa) {
		return getPdtProcDAO().countListaComplementoCid(ddtSeq, strPesquisa);
	}
	
	@Override
	public List<PdtComplementoPorCid> obterListaComplementoCidAtivos(Integer ddtSeq, Integer cidSeq) {
		return getPdtComplementoPorCidDAO().obterListaComplementoCidAtivos(ddtSeq, cidSeq);
	}
	
	@Override
	public List<PdtCidDesc> pesquisarPdtCidDescPorDdtSeqComCidAtivo(Integer seq) {
		return getPdtCidDescDAO().pesquisarPdtCidDescPorDdtSeqComCidAtivo(seq);
	}
	
	@Override
	public void excluirPdtCidDesc(PdtCidDesc cidDesc) throws BaseException {
		getPdtCidDescRN().excluir(cidDesc);
	}

	@Override
	public void persistirPdtCidDesc(PdtCidDesc cidDesc) throws BaseException {
		getPdtCidDescRN().persistirPdtCidDesc(cidDesc);
	}

	@Override
	public PdtCidDesc obterPdtCidDesc(PdtCidDescId id){
		return getPdtCidDescDAO().obterPorChavePrimaria(id);
	}
	
	@Override
	public void validarTempoMinimoCirurgia(Date dthrInicio, Date dthrFim, Short tempoMinimo) throws ApplicationBusinessException {
		getLiberacaoLaudoPreliminarON().validarTempoMinimoCirurgia(dthrInicio, dthrFim, tempoMinimo);
	}
	
	@Override
	public void liberarLaudoPreliminar(PdtDescricao descricao, Short unfSeq) throws BaseException {
		getLiberacaoLaudoPreliminarON().liberarLaudoPreliminar(descricao, unfSeq);
	}
	
	protected LiberacaoLaudoPreliminarON getLiberacaoLaudoPreliminarON() {
		return liberacaoLaudoPreliminarON;
	}

	@Override
	public void validarResultadoNormalOuCid(final Boolean resultadoNormal, final AghCid cid) throws BaseException {
		getPdtCidDescON().validarResultadoNormalOuCid(resultadoNormal, cid);
	}
	
	@Override
	public CertificarRelatorioCirurgiasPdtVO liberarLaudoDefinitivo(final Integer ddtSeq, final Integer crgSeq, final Short unfSeq, final Date dtExecucao, final DominioTipoDocumento tipoDocumento) 
			throws BaseException {
		return getDescricaoProcDiagTerapRN().liberarLaudoDefinitivo(ddtSeq, crgSeq, unfSeq, dtExecucao, tipoDocumento);
	}
	
	@Override
	@BypassInactiveModule
	public List<ProcedimentosPOLVO> pesquisarProcedimentosPDTComDescricaoPOL(Integer pacCodigo) {
		return this.getPdtProcDiagTerapDAO().pesquisarProcedimentosPDTComDescricaoPOL(pacCodigo);
	}
	
	@Override
	public void validarDatasDadoDesc(final Date dthrInicioProcedimento, final Date dthrFimProcedimento, final DescricaoProcDiagTerapVO descricaoProcDiagTerapVO)
			throws ApplicationBusinessException {
		getLiberacaoLaudoPreliminarON().validarDatasDadoDesc(dthrInicioProcedimento, dthrFimProcedimento, descricaoProcDiagTerapVO);
	}
	
	public boolean ultrapassaTempoMinimoCirurgia(final Date dthrInicioProcedimento, final Date dthrFimProcedimento, final DescricaoProcDiagTerapVO descricaoProcDiagTerapVO) {
		return getLiberacaoLaudoPreliminarON().ultrapassaTempoMinimoCirurgia(dthrInicioProcedimento, dthrFimProcedimento, descricaoProcDiagTerapVO);
	}
	
	public PdtDescricao obterPdtDescricaoEAtendimentoPorSeq(Integer seq){
		return getPdtDescricaoDAO().obterPdtDescricaoEAtendimentoPorSeq(seq);
	}
	
	protected PdtInstrDescRN getPdtInstrDescRN() { 
		return pdtInstrDescRN;
	}
	
	protected PdtProfRN getPdtProfRN() {
		return pdtProfRN;
	}
	
	protected PdtDescricaoRN getPdtDescricaoRN() {
		return pdtDescricaoRN;
	}
	
	protected PdtProcRN getPdtProcRN() {
		return pdtProcRN;
	}

	protected PdtAvalPreSedacaoRN getPdtAvalPreSedacaoRN() {
		return pdtAvalPreSedacaoRN;
	}
	
	protected PdtSolicTempRN getDescricaoProcDiagTerapIndicacaoRN() {
		return pdtSolicTempRN;
	}
	
	protected PdtMedicDescRN getPdtMedicDescRN() {
		return pdtMedicDescRN;
	}
	
	protected PdtCidDescON getPdtCidDescON() {
		return pdtCidDescON;
	}
	
	protected DescricaoProcDiagTerapEquipeON getDescricaoProcDiagTerapEquipeON() {
		return descricaoProcDiagTerapEquipeON;
	}
		
	protected PdtCidPorProcON getCidsProcedimentoON() {
		return pdtCidPorProcON;
	}

	protected PdtMedicUsualON getPdtMedicUsualON() {
		return pdtMedicUsualON;
	}
	
	protected GruposAchadosON getGruposAchadosON() {
		return gruposAchadosON;
	}
	
	protected DescricaoProcDiagTerapProcedimentoON getDescricaoProcDiagTerapProcedimentoON() {
		return descricaoProcDiagTerapProcedimentoON;
	}
	
	protected PdtTecnicaON getPdtTecnicaON() {
		return pdtTecnicaON;
	}
	
	protected PdtTecnicaPorProcON getPdtTecnicaPorProcON() {
		return pdtTecnicaPorProcON;
	}
	
	protected PdtProcDiagTerapRN getProcDiagTerapRN() {
		return pdtProcDiagTerapRN;
	}
	
	protected PdtDescObjetivaRN getPdtDescObjetivaRN() {
		return pdtDescObjetivaRN;
	}
	
	protected PdtCidDescRN getPdtCidDescRN() {
		return pdtCidDescRN;
	}

	protected PdtNotaAdicionalRN getPdtNotaAdicionalRN() {
		return pdtNotaAdicionalRN;
	}

	protected PdtDescObjetivaDAO getPdtDescObjetivaDAO() {
		return pdtDescObjetivaDAO;
	}

	protected PdtNotaAdicionalJnDAO getPdtNotaAdicionalJnDAO() {
		return pdtNotaAdicionalJnDAO;
	}
	
	protected PdtNotaAdicionalDAO getPdtNotaAdicionalDAO() {
		return pdtNotaAdicionalDAO;
	}
	
	protected PdtCidDescDAO getPdtCidDescDAO() {
		return pdtCidDescDAO;
	}

	protected PdtDadoImgDAO getPdtDadoImgDAO() {
		return pdtDadoImgDAO;
	}

	protected PdtImgDAO getPdtImgDAO() {
		return pdtImgDAO;
	}

	protected PdtDescPadraoDAO getPdtDescPadraoDAO() {
		return pdtDescPadraoDAO;
	}

	protected PdtDescTecnicaDAO getPdtDescTecnicaDAO() {
		return pdtDescTecnicaDAO;
	}

	protected PdtDadoDescDAO getPdtDadoDescDAO() {
		return pdtDadoDescDAO;
	}
	
	protected PdtProfDAO getPdtProfDAO() {
		return pdtProfDAO;
	}

	protected PdtProcDiagTerapDAO getPdtProcDiagTerapDAO(){
		return pdtProcDiagTerapDAO;
	}
	
	protected PdtProcDAO getPdtProcDAO() {
		return pdtProcDAO;
	}
	
	protected PdtAvalPreSedacaoDAO getPdtAvalPreSedacaoDAO() {
		return pdtAvalPreSedacaoDAO;
	}
	
	protected PdtViaAereasDAO getPdtViaAereasDAO() {
		return pdtViaAereasDAO;
	}
	
	protected PdtEquipamentoDAO getPdtEquipamentoDAO() {
		return pdtEquipamentoDAO;
	}

	protected PdtEquipPorProcDAO getPdtEquipPorProcDAO() {
		return pdtEquipPorProcDAO;
	}	

	protected PdtTecnicaPorProcDAO getPdtTecnicaPorProcDAO() {
		return pdtTecnicaPorProcDAO;
	}

	protected PdtTecnicaPorProcJnDAO getPdtTecnicaPorProcJnDAO() {
		return pdtTecnicaPorProcJnDAO;
	}

	protected PdtTecnicaDAO getPdtTecnicaDAO() {
		return pdtTecnicaDAO;
	}

	protected PdtAchadoDAO getPdtAchadoDAO() {
		return pdtAchadoDAO;
	}

	protected PdtGrupoDAO getPdtGrupoDAO() {
		return pdtGrupoDAO;
	}

	protected PdtMedicUsualDAO getPdtMedicUsualDAO() {
		return pdtMedicUsualDAO;
	}
	
	protected PdtCidPorProcDAO getPdtCidPorProcDAO() {
		return pdtCidPorProcDAO;
	}

	protected PdtComplementoPorCidDAO getPdtComplementoPorCidDAO() {
		return pdtComplementoPorCidDAO;
	}

	protected PdtSolicTempDAO getPdtSolicTempDAO() {
		return pdtSolicTempDAO;
	}

	protected PdtMedicDescDAO getPdtMedicDescDAO() {
		return pdtMedicDescDAO;
	}

	protected PdtDescricaoDAO getPdtDescricaoDAO() {
		return pdtDescricaoDAO;
	}

	protected PdtInstrDescDAO getPdtInstrDescDAO() {
		return pdtInstrDescDAO;
	}
	
	protected CancelamentoDescricaoCirurgicaPdtRN getCancelamentoDescricaoCirurgicaPdtRN() {
		return cancelamentoDescricaoCirurgicaPdtRN;
	}

}
 