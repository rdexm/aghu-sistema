package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.blococirurgico.vo.CertificarRelatorioCirurgiasPdtVO;
import br.gov.mec.aghu.blococirurgico.vo.ComplementoCidVO;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoProcDiagTerapVO;
import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
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
import br.gov.mec.aghu.model.PdtComplementoPorCid;
import br.gov.mec.aghu.model.PdtDadoDesc;
import br.gov.mec.aghu.model.PdtDadoImg;
import br.gov.mec.aghu.model.PdtDescObjetiva;
import br.gov.mec.aghu.model.PdtDescPadrao;
import br.gov.mec.aghu.model.PdtDescTecnica;
import br.gov.mec.aghu.model.PdtDescricao;
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
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@SuppressWarnings({ "PMD.AghuTooManyMethods","PMD.CouplingBetweenObjects" })
public interface IBlocoCirurgicoProcDiagTerapFacade extends Serializable {
	
	PdtDescricao carregarDescricaoProcDiagTerap(Integer crgSeq) throws ApplicationBusinessException;
	
	void inserirProf(Integer ddtSeq, ProfDescricaoCirurgicaVO profDescricaoCirurgicaVO
			) throws ApplicationBusinessException;
	
	void removerProf(PdtProf oldProf);
	
	void desfazCarregamentoDescricaoCirurgicaPDT(final Integer crgSeq, final Integer ddtSeq) throws ApplicationBusinessException, ApplicationBusinessException;
	
	boolean habilitarNotasAdicionais(final Integer crgSeq, final PdtDescricao descricao) throws ApplicationBusinessException;
	
	List<PdtNotaAdicional> pesquisarNotaAdicionalPorDdtSeq(Integer ddtSeq);
	
	List<PdtNotaAdicional> pesquisarNotaAdicionalPorDdtSeqESeqpDesc(Integer seqPdtDescricao);
	
	PdtNotaAdicional obterPdtNotaAdicionalPorServidorEPdtDescricao(RapServidores servidor, Integer ddtSeq);
	
	void persistirPdtNotaAdicional(final PdtNotaAdicional notaAdicional) throws ApplicationBusinessException;
	
	void excluirPdtNotaAdicional(final PdtNotaAdicional notaAdicional) throws ApplicationBusinessException;

	List<PdtDescObjetiva> pesquisarPdtDescObjetivaPorDdtSeq(Integer seq);
	
	List<PdtDadoImg> pesquisarPdtDadoImgPorDdtSeq(Integer ddtSeq);
	
	List<PdtImg> pesquisarImagens(Integer cirSeq);
	
	Long verificarSeTemImagem(Integer ddtSeq);

	PdtImg obterPdtImgPorDdtSeqESeq(Short seqp, Integer ddtSeq);
	
	Long pesquisarDescricaoPadraoCount(Short especialidadeId, Integer procedimentoId, String titulo);
	
	List<PdtDescPadrao> pesquisarDescricaoPadrao(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Short especialidadeId, Integer procedimentoId, String titulo);

	PdtDescPadrao obterDescricaoPadraoById(Short seqp, Short espSeq);

	PdtDescTecnica obterDescricaoTecnicaPorDdtSeq(Integer ddtSeq);
	
	PdtDescTecnica obterDescricaoTecnicaPorChavePrimaria(Integer seq);
	
	void persistirPdtDescTecnica(final PdtDescTecnica descricaoTecnica) throws ApplicationBusinessException;
	
	void excluirPdtDescTecnica(final PdtDescTecnica descricaoTecnica) throws ApplicationBusinessException;

	PdtDadoDesc obterDadoDescPorDdtSeq(Integer seq);
	
	PdtDadoDesc obterDadoDescPorChavePrimaria(Integer seq);
	
	void inserirDadoDesc(PdtDadoDesc newDadoDesc) 
			throws ApplicationBusinessException;
	
	PdtDadoDesc atualizarDadoDesc(PdtDadoDesc newDadoDesc) throws ApplicationBusinessException;
	
	RapServidores buscaRapServidorDePdtProfissao(Integer crgSeq,DominioTipoAtuacao tipoAtuacao);

	List<PdtProf> pesquisarProfPorDdtSeq(Integer ddtSeq);

	List<PdtProf> buscaPdtProfissaoPorDdtSeqETipoAtuacao(Integer ddtSeq,
			DominioTipoAtuacao tipoAtuacao, Integer serMatricula);
	
	List<PdtProf> pesquisarProfPorDdtSeqETipoAtuacao(Integer ddtSeq, DominioTipoAtuacao tipoAtuacao);
	
	List<PdtProf> pesquisarProfPorDdtSeqEListaTipoAtuacao(Integer ddtSeq, List<DominioTipoAtuacao> listaTipoAtuacao);
	
	List<PdtProf> pesquisarProfPorDdtSeqServidorETipoAtuacao(Integer ddtSeq, Integer serMatricula, Short serVinCodigo, DominioTipoAtuacao tipoAtuacao);
	
	Short obterMaiorSeqpProfPorDdtSeq(Integer ddtSeq);

	String obterNomePessoaPdtProfByPdtDescricao(Integer seqPdtDescricao, Integer seqMbcCirurgia);
	
	List<PdtProcDiagTerap> listarProcDiagTerap(Object objPesquisa);

	Long listarProcDiagTerapCount(Object objPesquisa);

	Long pesquisarPdtProcDiagTerapCount(String strPesquisa);
	
	List<PdtProcDiagTerap> pesquisarPdtProcDiagTerap(String strPesquisa);
	
	PdtProcDiagTerap obterPdtProcDiagTerapPorChavePrimaria(Integer seq);

	PdtProcDiagTerap obterPdtProcDiagTerap(Integer seqPdtProcDiagTerap);

	Long listarProcDiagTerapAtivaPorDescricaoCount(Object strPesquisa);

	void inserirProcDiagTerap(PdtProcDiagTerap newProcDiagTerap);
	
	void atualizarProcDiagTerap(PdtProcDiagTerap newProcDiagTerap);
	
	List<PdtProcDiagTerap> listarProcDiagTerapAtivaPorDescricao(Object strPesquisa);
	
	List<AghEspecialidades> pesquisarEspecialidadeDescricaoPadraoProcCirurgicoAtivo();
	
	List<MbcProcedimentoCirurgicos> pesquisarProcCirurgicoAtivoDescricaoPadraoPorEspSeq(Short espSeq);
	
	List<PdtDescPadrao> pesquisarDescPadraoProcedimentoCirurgicoAtivoPorEspSeqEPciSeq(Short espSeq, Integer pciSeq);

	List<PdtProc> pesquisarPdtProcPorDdtSeq(Integer seq);
	
	PdtAvalPreSedacao pesquisarPdtAvalPreSedacaoPorDdtSeq(Integer seq);
	
	List<PdtViaAereas> obterViasAereas();
	
	List<PdtProc> pesquisarProcComProcedimentoCirurgicoAtivoPorDdtSeq(Integer ddtSeq);

	PdtProc obterPdtProcPorChavePrimaria(PdtProcId id);
	
	void excluirPdtProc(final PdtProc pdtProc);
	
	void persistirPdtProc(final PdtProc pdtProc)throws ApplicationBusinessException;
	
	void persistirPdtAvalPreSedacao(final PdtAvalPreSedacao pdtAvalPreSedacao)throws ApplicationBusinessException;
	
	
	List<PdtProc> pesquisarPdtProcPorDdtSeqOrdenadoPorSeqP(Integer seq);
	
	PdtEquipamento obterEquipamentoPorChavePrimaria(Short seq);

	List<PdtEquipamento> pesquisarEquipamentosDiagnosticoTerapeutico(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String descricao, Short codigo,
			DominioSituacao situacao, String enderecoImagens);


	Long pesquisarEquipamentosDiagnosticoTerapeuticoCount(String descricao,
			Short codigo, DominioSituacao situacao, String enderecoImagens);
	
	List<PdtEquipamento> pesquisarEquipamentoPorNome(Object strPesquisa);

	PdtEquipamento obterPdtEquipamentoPorSeq(Short seq);
	
	List<PdtEquipamento> listarPdtEquipamentoAtivoPorDescricao(Object strPesquisa);
	Long listarPdtEquipamentoAtivoPorDescricaoCount(Object strPesquisa);
	List<PdtEquipamento> pesquisarEquipamentosDiagnosticoTerapeutico(final Integer dptSeq);

	Long pesquisarEquipamentoPorNomeCount(Object strPesquisa);
	
	List<PdtEquipPorProc> pesquisarPdtEquipPorProcPorDptSeq(Integer dptSeq);
	
	List<PdtEquipPorProc> listarPdtEquipPorProcAtivoPorEquipe(Short seq);
	
	
	
	
	List<PdtTecnicaPorProc> listarPdtTecnicaPorProc(Integer dteSeq);

	List<PdtTecnicaPorProc> pesquisarPdtTecnicaPorProcPorDptSeq(Integer dptSeq);

	PdtTecnicaPorProc obterOriginalPdtTecnicaPorProc(PdtTecnicaPorProc tecnicaPorProc);
	
	void refreshPdtTecnicaPorProc(List<PdtTecnicaPorProc> listPdtTecnicasPorProc);

	String removerPdtTecnicaPorProc(PdtTecnicaPorProc tecnicaPorProc
			);
	
	String persistirPdtTecnicaPorProc(PdtTecnica tecnica,
			PdtProcDiagTerap procDiagTerap)
			throws ApplicationBusinessException;	
	
	
	PdtTecnica obterTecnicaPorChavePrimaria(Integer seq);
	
	List<PdtTecnica> pesquisarTecnicaPorDescricaoOuSeq(Object strPesquisa);	
	
	List<PdtTecnica> listarPdtTecnicaPorSeqDescricaoSituacao(Integer seq, String descricao, DominioSituacao situacao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	
	Long listarPdtTecnicaPorSeqDescricaoSituacaoCount(Integer seq, String descricao, DominioSituacao situacao);
	
	List<PdtTecnica> listarPdtTecnicaPorDptSeq(final Integer dptSeq);
	
	String persistirPdtTecnica(PdtTecnica tecnica) throws ApplicationBusinessException;
	
	List<PdtAchado> pesquisarPdtAchados(Integer dgrDptSeq, Short dgrSeqp);

	void refreshPdtAchado(List<PdtAchado> listaAchados);
	
	String gravarPdtAchado(PdtGrupo grupo, PdtAchado achado) throws ApplicationBusinessException;

	PdtGrupo obterPdtGrupoPorId(PdtGrupoId grupoId);
	
	String gravarPdtGrupo(PdtGrupo grupo, Integer dptSeq) throws ApplicationBusinessException;
	
	List<PdtGrupo> pesquisarPdtGrupoPorIdDescricaoSituacao(Integer dptSeq, Short seqp, String descricao, DominioSituacao indSituacao,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc);

	Long pesquisarPdtGrupoPorIdDescricaoSituacaoCount(Integer dptSeq, Short seqp, String descricao, DominioSituacao indSituacao);


	String excluirPdtMedicUsual(PdtMedicUsual pdtMedicUsualDelecao
			) throws ApplicationBusinessException;
	
	String persistirPdtMedicUsual(PdtMedicUsual pdtMedicUsual
			) throws ApplicationBusinessException;

	List<PdtMedicUsual> pesquisaPdtMedicUsual(PdtMedicUsual pdtMedicUsual);
	
	List<PdtCidPorProc> listarPdtCidPorProcPorProcedimentoSituacaoCid(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, PdtProcDiagTerap procedimento, DominioSituacao situacao, AghCid cid);	
	
	Long listarPdtCidPorProcPorProcedimentoSituacaoCidCount(PdtProcDiagTerap procedimento, DominioSituacao situacao, AghCid cid);
	
	PdtCidPorProc obterPdtCidPorProcPorChavePrimaria(Integer dptSeq, Integer cidSeq);
	
	String persistirCidProcedimento(PdtCidPorProc cidProcedimento, Integer dptSeq, Integer cidSeq) throws ApplicationBusinessException;
	
	List<PdtComplementoPorCid> listarPdtComplementoPorCids(Integer pdtSeq, Integer cidSeq);
	
	String persistirComplemento(PdtComplementoPorCid complemento, Integer dptSeq, Integer cidSeq) throws ApplicationBusinessException;
	
	PdtSolicTemp obterSolicTempPorDdtSeq(Integer seq);

	List<PdtMedicDesc> pesquisarMedicDescPorDdtSeq(Integer seq);
	
	List<PdtMedicDesc> pesquisarMedicDescPorDdtSeqOrdenadoPorDdtSeqESeqp(Integer seq);

	PdtMedicDesc obterPdtMedicDescPorChavePrimaria(PdtMedicDescId pdtMedicDescId);

	void persistirPdtMedicDesc(final PdtMedicDesc medicDescricao) throws ApplicationBusinessException;
	
	void excluirPdtMedicDesc(final PdtMedicDesc medicDescricao) throws ApplicationBusinessException;
	
	List<PdtDescricao> listarDescricaoPorSeqCirurgiaSituacao(Integer crgSeq, DominioSituacaoDescricao[] situacao);

	List<PdtDescricao> listarDescricaoPorSeqCirurgiaSituacao(Integer crgSeq, DominioSituacaoDescricao[] situacao, PdtDescricao.Fields order);
	Long listarDescricaoPorSeqCirurgiaSituacaoCount(Integer crgSeq, DominioSituacaoDescricao[] situacao);

	List<PdtDescricao> listarDescricaoPorSeqCirurgia(Integer seqCirurgia);

	PdtDescricao obterPdtDescricao(Integer seqPdtDescricao, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);
	
	List<PdtDescricao> pesquisarDescricaoPorCirurgiaEServidor(Integer crgSeq, Integer serMatricula, Short serVinCodigo);
	
	void persistirIndicacao(PdtSolicTemp solicitacao);
		
	void atualizarDescricao(final PdtDescricao newDescricao) throws BaseException;
	
	void validarContaminacaoProcedimentoCirurgicoPdt(Integer pciSeq, DominioIndContaminacao novoIndContaminacao) throws ApplicationBusinessException;	
		
	List<PdtInstrDesc> pesquisarPdtInstrDescPorDdtSeq(final Integer ddtSeq);

	PdtInstrDesc obterPdtInstrDescPorChavePrimaria(PdtInstrDescId pdtInstrDescId);

	void excluirPdtInstrDesc(PdtInstrDesc instrDesc) throws ApplicationBusinessException;
	
	void inserirPdtInstrDesc(final PdtInstrDesc instrucaoDescritiva) throws ApplicationBusinessException;

	List<ComplementoCidVO> obterListaComplementoCid(Integer ddtSeq,
			Object strPesquisa);
	
	Long obterListaComplementoCidCount(Integer ddtSeq, 
			Object strPesquisa);

	List<PdtComplementoPorCid> obterListaComplementoCidAtivos(Integer ddtSeq,
			Integer cidSeq);

	List<PdtCidDesc> pesquisarPdtCidDescPorDdtSeqComCidAtivo(Integer seq);

	void excluirPdtCidDesc(PdtCidDesc cidDesc)
			throws BaseException;

	PdtCidDesc obterPdtCidDesc(PdtCidDescId id);

	void persistirPdtCidDesc(PdtCidDesc cidDesc)
			throws BaseException;

	Long countListaComplementoCid(Integer ddtSeq, Object strPesquisa);
	
	void validarTempoMinimoCirurgia(Date dthrInicio, Date dthrFim, Short tempoMinimo) throws ApplicationBusinessException;

	void validarResultadoNormalOuCid(Boolean resultadoNormal, AghCid cid)
			throws BaseException;
	
	void liberarLaudoPreliminar(PdtDescricao descricao, Short unfSeq) throws BaseException;			
			
	CertificarRelatorioCirurgiasPdtVO liberarLaudoDefinitivo(Integer ddtSeq,
			Integer crgSeq, Short unfSeq, Date dtExecucao,
			DominioTipoDocumento tipoDocumento)
			throws BaseException;
		
	PdtProf obterPdtProfPorChavePrimaria(PdtProfId pdtProfId);

	List<ProcedimentosPOLVO> pesquisarProcedimentosPDTComDescricaoPOL(
			Integer pacCodigo);
	
	void validarDatasDadoDesc(final Date dthrInicioProcedimento, final Date dthrFimProcedimento, final DescricaoProcDiagTerapVO descricaoProcDiagTerapVO) 
			throws ApplicationBusinessException;
	
	boolean ultrapassaTempoMinimoCirurgia(final Date dthrInicioProcedimento, final Date dthrFimProcedimento, final DescricaoProcDiagTerapVO descricaoProcDiagTerapVO);

	public PdtDescricao obterPdtDescricaoEAtendimentoPorSeq(Integer ddtSeq);
}