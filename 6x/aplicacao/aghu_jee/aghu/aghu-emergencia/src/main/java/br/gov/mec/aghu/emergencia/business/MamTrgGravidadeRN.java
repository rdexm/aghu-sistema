package br.gov.mec.aghu.emergencia.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.ambulatorio.dao.MamTrgExameDAO;
import br.gov.mec.aghu.emergencia.dao.MamDescritorDAO;
import br.gov.mec.aghu.emergencia.dao.MamObrigatoriedadeDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgGeralDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgGravidadeDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgMedicacaoDAO;
import br.gov.mec.aghu.emergencia.dao.MamTriagensJnDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.emergencia.vo.DescritorTrgGravidadeVO;
import br.gov.mec.aghu.emergencia.vo.TrgGravidadeFluxogramaVO;
import br.gov.mec.aghu.model.MamFluxograma;
import br.gov.mec.aghu.model.MamItemExame;
import br.gov.mec.aghu.model.MamItemGeral;
import br.gov.mec.aghu.model.MamItemMedicacao;
import br.gov.mec.aghu.model.MamObrigatoriedade;
import br.gov.mec.aghu.model.MamTrgExames;
import br.gov.mec.aghu.model.MamTrgExamesId;
import br.gov.mec.aghu.model.MamTrgGerais;
import br.gov.mec.aghu.model.MamTrgGeralId;
import br.gov.mec.aghu.model.MamTrgGravidade;
import br.gov.mec.aghu.model.MamTrgGravidadeId;
import br.gov.mec.aghu.model.MamTrgMedicacoes;
import br.gov.mec.aghu.model.MamTrgMedicacoesId;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Regras de negócio de MamTrgGravidade
 * 
 * @author luismoura
 * 
 */
@Stateless
public class MamTrgGravidadeRN extends BaseBusiness {
	private static final long serialVersionUID = -6781526900905284611L;

	@Inject
	private MamTrgGravidadeDAO mamTrgGravidadeDAO;

	@Inject
	private MamTriagensJnDAO mamTriagensJnDAO;

	@Inject
	private MamDescritorDAO mamDescritorDAO;

	@Inject
	MamObrigatoriedadeDAO mamObrigatoriedadeDAO;

	@Inject
	private MamTrgExameDAO mamTrgExameDAO;

	@Inject
	private MamTrgMedicacaoDAO mamTrgMedicacaoDAO;

	@Inject
	private MamTrgGeralDAO mamTrgGeralDAO;
	
	@Inject
	private RapServidoresDAO servidorDAO;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;

	@Override
	protected Log getLogger() {
		return null;
	}

	private enum MamTrgGravidadeRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SUCESSO_SELECAO_GRAVIDADE, //
		MENSAGEM_ERRO_DESCRITOR_SELECAO, //
		MENSAGEM_INFO_CHECAGEM_LOTE, //
		;
	}

	/**
	 * Verifica se deve desconsiderar a classificação de risco para o paciente em questão uma vez que o mesmo tenha sido transferido de unidade e já tenham feito classificação de
	 * risco
	 * 
	 * RN06 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param trgSeqAcolhimento
	 * @param unfSeqAcolhimento
	 * @return
	 */
	public TrgGravidadeFluxogramaVO obterClassificacaoRiscoPaciente(Long trgSeqAcolhimento, Short unfSeqAcolhimento) {

		// Executar a consulta C14 passando como parâmetro o SEQ de MAM_TRIAGENS obtido da estória #29856 – Realizar Acolhimento
		MamTrgGravidade ultimaGravidade = null;
		if (trgSeqAcolhimento != null) {
			ultimaGravidade = mamTrgGravidadeDAO.obterUltimaGravidadePorTriagem(trgSeqAcolhimento);
		}

		// Se a consulta C14 não retornar resultados abrir a modal da imagem 2 com os campos limpos
		if (ultimaGravidade == null) {
			return null;
		}

		// Se a consulta C14 retornar resultados executar os seguintes passos
		// 1. Executar a consulta C4
		List<Short> unidades = mamTriagensJnDAO.obterUltimasTriagensJnPorTrgSeq(trgSeqAcolhimento);

		// Se consulta C4 retornar resultados:
		if (unidades != null && !unidades.isEmpty()) {
			// 1.  Se o UNF_SEQ do segundo resultado (se existente) de C4 for igual ao UNF_SEQ do acolhimento executar a RN10
			if (unidades.size() > 1 && unidades.get(1).equals(unfSeqAcolhimento)) {
				return this.obterFluxogramaClassificacaoRiscoPaciente(ultimaGravidade);
			}
			// Se o UNF_SEQ do segundo resultado de C4 NÃO FOR IGUAL ao UNF_SEQ do acolhimento
			// - Desconsiderar a classificação de risco (Apresentar modal da imagem 2 com os campos limpos)
			return null;
		}
		// Se a consulta C4 não retornar resultados:
		// 1. Abrir a modal da imagem 2 e executar RN10 para preencher os campos
		return this.obterFluxogramaClassificacaoRiscoPaciente(ultimaGravidade);
	}

	/**
	 * Regras para obter os dados de uma classificação anteriormente feita
	 * 
	 * RN10 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param unfSeq
	 * @return
	 */
	private TrgGravidadeFluxogramaVO obterFluxogramaClassificacaoRiscoPaciente(MamTrgGravidade ultimaGravidade) {
		TrgGravidadeFluxogramaVO result = null;

		// 1. Obter o primeiro registro retornado da consulta C14 (primeiro passo da RN06)
		// OK, parâmetro ultimaGravidade

		// 2. Executar consulta C15 passando como parâmetro GRV_SEQ e DCT_SEQ obtidos no passo anterior
		if (ultimaGravidade != null) {
			Short grvSeq = ultimaGravidade.getMamGravidade() != null ? ultimaGravidade.getMamGravidade().getSeq() : null;
			Integer dctSeq = ultimaGravidade.getMamDescritor() != null ? ultimaGravidade.getMamDescritor().getSeq() : null;
			MamFluxograma fluxograma = mamDescritorDAO.obterFluxogramaDoDescritorPorGravidade(grvSeq, dctSeq);
			if (fluxograma != null) {
				result = new TrgGravidadeFluxogramaVO();
				result.setTrgGravidade(ultimaGravidade);
				result.setFluxograma(fluxograma);
			}
		}
		return result;
	}

	/**
	 * Regras para botão Gravar (Obs.: Sempre que esse botão for clicado deve-se inserir um novo registro em MAM_TRG_GRAVIDADES.
	 * 
	 * RN02 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param item
	 * @throws ApplicationBusinessException
	 */
	public void gravarMamTrgGravidade(Long trgSeq, DescritorTrgGravidadeVO item, String micNome) throws ApplicationBusinessException {
		// 1. Sistema não deve deixar gravar se não haver um descritor selecionado (somente um radio button ‘Sim’ marcado)
		// Apresentar mensagem MENSAGEM_ERRO_DESCRITOR_SELECAO
		if (item == null) {
			throw new ApplicationBusinessException(MamTrgGravidadeRNExceptionCode.MENSAGEM_ERRO_DESCRITOR_SELECAO);
		}

		MamTrgGravidade mamTrgGravidade = new MamTrgGravidade();
		mamTrgGravidade.setMamDescritor(item.getDescritor());
		mamTrgGravidade.setMamGravidade(item.getDescritor().getGravidade());

		// 2. Executa consulta C3 para obter o SEQP para inserção da gravidade
		this.popularIdMamTrgGravidade(mamTrgGravidade, trgSeq);

		// 3. Executa RN03
		this.preInsert(mamTrgGravidade, micNome);

		// 4. Executa inserção I1
		mamTrgGravidadeDAO.persistir(mamTrgGravidade);

		this.atualizarItensObrigatorios(trgSeq, item, micNome);

	}

	/**
	 * Atualiza os itens obrigatórios
	 * 
	 * RN07, RN08 e RN09
	 * 
	 * @param trgSeq
	 * @param item
	 * @param micNome
	 */
	private void atualizarItensObrigatorios(Long trgSeq, DescritorTrgGravidadeVO item, String micNome) {
		List<MamItemExame> itensExame = new ArrayList<MamItemExame>();
		List<MamItemMedicacao> itensMedicacao = new ArrayList<MamItemMedicacao>();
		List<MamItemGeral> itensGerais = new ArrayList<MamItemGeral>();

		this.popularItensAtivos(item, itensExame, itensMedicacao, itensGerais);

		List<MamItemExame> itensExameInseridos = new ArrayList<MamItemExame>();
		List<MamItemMedicacao> itensMedicacaoInseridos = new ArrayList<MamItemMedicacao>();
		List<MamItemGeral> itensGeraisInseridos = new ArrayList<MamItemGeral>();

		this.popularItensInseridos(trgSeq, itensExameInseridos, itensMedicacaoInseridos, itensGeraisInseridos);

		Date dataInclusao = new Date();

		this.atualizarItensExame(trgSeq, micNome, itensExame, itensExameInseridos, dataInclusao);
		this.atualizarItensMedicacao(trgSeq, micNome, itensMedicacao, itensMedicacaoInseridos, dataInclusao);
		this.atualizarItensGeral(trgSeq, micNome, itensGerais, itensGeraisInseridos, dataInclusao);
	}

	/**
	 * Atualiza os itens de exame obrigatórios
	 * 
	 * RN07
	 * 
	 * @param trgSeq
	 * @param micNome
	 * @param itensExame
	 * @param itensExameInseridos
	 * @param dataInclusao
	 */
	private void atualizarItensExame(Long trgSeq, String micNome, List<MamItemExame> itensExame, List<MamItemExame> itensExameInseridos, Date dataInclusao) {
		itensExame.removeAll(itensExameInseridos);
		for (MamItemExame itemExame : itensExame) {
			this.persistirItemExame(trgSeq, micNome, itemExame, dataInclusao);
		}
	}

	/**
	 * Persiste um item de exame
	 * 
	 * @param trgSeq
	 * @param micNome
	 * @param itemExame
	 * @param dataInclusao
	 */
	private void persistirItemExame(Long trgSeq, String micNome, MamItemExame itemExame, Date dataInclusao) {
		MamTrgExames mamTrgExames = new MamTrgExames();
		this.popularIdMamTrgExames(mamTrgExames, trgSeq);
		mamTrgExames.setItemExame(itemExame);
		mamTrgExames.setComplemento(null);
		mamTrgExames.setDthrInformada(dataInclusao);
		mamTrgExames.setCriadoEm(dataInclusao);
		mamTrgExames.setSerVinCodigo(usuario.getVinculo());
		mamTrgExames.setSerMatricula(usuario.getMatricula());
		mamTrgExames.setMicNome(micNome);
		mamTrgExames.setIndUso(false);
		mamTrgExames.setIndConsistenciaOk(false);
		mamTrgExameDAO.persistir(mamTrgExames);
	}

	/**
	 * Atualiza os itens de medicacao obrigatórios
	 * 
	 * RN08
	 * 
	 * @param trgSeq
	 * @param micNome
	 * @param itensMedicacao
	 * @param itensMedicacaoInseridos
	 * @param dataInclusao
	 */
	private void atualizarItensMedicacao(Long trgSeq, String micNome, List<MamItemMedicacao> itensMedicacao, List<MamItemMedicacao> itensMedicacaoInseridos,
			Date dataInclusao) {
		itensMedicacao.removeAll(itensMedicacaoInseridos);
		for (MamItemMedicacao itemMedicacao : itensMedicacao) {
			this.persistirItemMedicacao(trgSeq, micNome, itemMedicacao, dataInclusao);
		}
	}

	/**
	 * Persiste um item de medicacao
	 * 
	 * @param trgSeq
	 * @param micNome
	 * @param itemMedicacao
	 * @param dataInclusao
	 */
	private void persistirItemMedicacao(Long trgSeq, String micNome, MamItemMedicacao itemMedicacao, Date dataInclusao) {
		MamTrgMedicacoes mamTrgMedicacoes = new MamTrgMedicacoes();
		this.popularIdMamTrgMedicacoes(mamTrgMedicacoes, trgSeq);
		mamTrgMedicacoes.setItemMedicacao(itemMedicacao);
		mamTrgMedicacoes.setComplemento(null);
		mamTrgMedicacoes.setDthrInformada(dataInclusao);
		mamTrgMedicacoes.setCriadoEm(dataInclusao);		
		mamTrgMedicacoes.setServidor(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		
		mamTrgMedicacoes.setMicNome(micNome);
		mamTrgMedicacoes.setUso(false);
		mamTrgMedicacoes.setConsistenciaOk(false);
		mamTrgMedicacaoDAO.persistir(mamTrgMedicacoes);
	}

	/**
	 * Atualiza os itens gerais obrigatórios
	 * 
	 * RN09
	 * 
	 * @param trgSeq
	 * @param micNome
	 * @param itensGerais
	 * @param itensGeraisInseridos
	 * @param dataInclusao
	 */
	private void atualizarItensGeral(Long trgSeq, String micNome, List<MamItemGeral> itensGerais, List<MamItemGeral> itensGeraisInseridos, Date dataInclusao) {
		itensGerais.removeAll(itensGeraisInseridos);
		for (MamItemGeral itemGeral : itensGerais) {
			this.persistirItemGeral(trgSeq, micNome, itemGeral, dataInclusao);
		}
	}

	/**
	 * Persiste um item de exame
	 * 
	 * @param trgSeq
	 * @param micNome
	 * @param itemGeral
	 * @param dataInclusao
	 */
	private void persistirItemGeral(Long trgSeq, String micNome, MamItemGeral itemGeral, Date dataInclusao) {
		MamTrgGerais mamTrgGerais = new MamTrgGerais();
		this.popularIdMamTrgGerais(mamTrgGerais, trgSeq);
		mamTrgGerais.setMamItemGeral(itemGeral);
		mamTrgGerais.setComplemento(null);
		mamTrgGerais.setDtHrInformada(dataInclusao);
		mamTrgGerais.setCriadoEm(dataInclusao);
		mamTrgGerais.setSerVinCodigo(usuario.getVinculo());
		mamTrgGerais.setSerMatricula(usuario.getMatricula());
		mamTrgGerais.setMicNome(micNome);
		mamTrgGerais.setIndUso(false);
		mamTrgGerais.setIndConsistenciaOk(false);
		mamTrgGeralDAO.persistir(mamTrgGerais);
	}

	/**
	 * Popula as listas de itens ativos
	 * 
	 * @param item
	 * @param itensExame
	 * @param itensMedicacao
	 * @param itensGerais
	 */
	private void popularItensAtivos(DescritorTrgGravidadeVO item, List<MamItemExame> itensExame, List<MamItemMedicacao> itensMedicacao,
			List<MamItemGeral> itensGerais) {
		List<MamObrigatoriedade> obrigatoriedades = mamObrigatoriedadeDAO.pesquisarObrigatoriedadesAtivasPorDescritor(item.getDescritor().getSeq());

		for (MamObrigatoriedade mamObrigatoriedade : obrigatoriedades) {
			if (mamObrigatoriedade.getMamItemExame() != null) {
				itensExame.add(mamObrigatoriedade.getMamItemExame());
			} else if (mamObrigatoriedade.getMamItemMedicacao() != null) {
				itensMedicacao.add(mamObrigatoriedade.getMamItemMedicacao());
			} else if (mamObrigatoriedade.getMamItemGeral() != null) {
				itensGerais.add(mamObrigatoriedade.getMamItemGeral());
			}
		}
	}

	/**
	 * Popula as listas de itens inseridos
	 * 
	 * @param trgSeq
	 * @param itensExameInseridos
	 * @param itensMedicacaoInseridos
	 * @param itensGeraisInseridos
	 */
	private void popularItensInseridos(Long trgSeq, List<MamItemExame> itensExameInseridos, List<MamItemMedicacao> itensMedicacaoInseridos,
			List<MamItemGeral> itensGeraisInseridos) {
		itensExameInseridos.addAll(this.obterItensExameInseridos(trgSeq));
		itensMedicacaoInseridos.addAll(this.obterItensMedicacaoInseridos(trgSeq));
		itensGeraisInseridos.addAll(this.obterItensGeraisInseridos(trgSeq));
	}

	private List<MamItemExame> obterItensExameInseridos(Long trgSeq) {
		List<MamTrgExames> exames = mamTrgExameDAO.pesquisarMamTrgExamesPorTriagem(trgSeq);
		List<MamItemExame> itensExameInseridos = new ArrayList<MamItemExame>();
		for (MamTrgExames exame : exames) {
			if (exame.getItemExame() != null) {
				itensExameInseridos.add(exame.getItemExame());
			}
		}
		return itensExameInseridos;
	}

	private List<MamItemMedicacao> obterItensMedicacaoInseridos(Long trgSeq) {
		List<MamTrgMedicacoes> medicacoes = mamTrgMedicacaoDAO.pesquisarMamTrgMedicacoesPorTriagem(trgSeq);
		List<MamItemMedicacao> itensMedicacaoInseridos = new ArrayList<MamItemMedicacao>();
		for (MamTrgMedicacoes medicacao : medicacoes) {
			if (medicacao.getItemMedicacao() != null) {
				itensMedicacaoInseridos.add(medicacao.getItemMedicacao());
			}
		}
		return itensMedicacaoInseridos;
	}

	private List<MamItemGeral> obterItensGeraisInseridos(Long trgSeq) {
		List<MamTrgGerais> gerais = mamTrgGeralDAO.pesquisarMamTrgGeraisPorTriagem(trgSeq);
		List<MamItemGeral> itensGeraisInseridos = new ArrayList<MamItemGeral>();
		for (MamTrgGerais geral : gerais) {
			if (geral.getMamItemGeral() != null) {
				itensGeraisInseridos.add(geral.getMamItemGeral());
			}
		}
		return itensGeraisInseridos;
	}

	/**
	 * Popula o ID de MamTrgGravidade
	 * 
	 * @param mamTrgGravidade
	 * @param trgSeq
	 */
	private void popularIdMamTrgGravidade(MamTrgGravidade mamTrgGravidade, Long trgSeq) {
		MamTrgGravidadeId id = new MamTrgGravidadeId();
		id.setTrgSeq(trgSeq);
		Short seqp = mamTrgGravidadeDAO.obterProximoSeqPorTriagem(trgSeq);
		id.setSeqp(seqp);
		mamTrgGravidade.setId(id);
	}

	/**
	 * Popula o ID de MamTrgExames
	 * 
	 * @param mamTrgExames
	 * @param trgSeq
	 */
	private void popularIdMamTrgExames(MamTrgExames mamTrgExames, Long trgSeq) {
		MamTrgExamesId id = new MamTrgExamesId();
		id.setTrgSeq(trgSeq);
		Short seqp = mamTrgExameDAO.obterProximoSeqPorTriagem(trgSeq);
		id.setSeqp(seqp);
		mamTrgExames.setId(id);
	}

	/**
	 * Popula o ID de MamTrgMedicacoes
	 * 
	 * @param mamTrgMedicacoes
	 * @param trgSeq
	 */
	private void popularIdMamTrgMedicacoes(MamTrgMedicacoes mamTrgMedicacoes, Long trgSeq) {
		MamTrgMedicacoesId id = new MamTrgMedicacoesId();
		id.setTrgSeq(trgSeq);
		Short seqp = mamTrgMedicacaoDAO.obterProximoSeqPorTriagem(trgSeq);
		id.setSeqp(seqp);
		mamTrgMedicacoes.setId(id);
	}

	/**
	 * Popula o ID de MamTrgGerais
	 * 
	 * @param mamTrgGravidade
	 * @param trgSeq
	 */
	private void popularIdMamTrgGerais(MamTrgGerais mamTrgGerais, Long trgSeq) {
		MamTrgGeralId id = new MamTrgGeralId();
		id.setTrgSeq(trgSeq);
		Short seqp = mamTrgGeralDAO.obterProximoSeqPorTriagem(trgSeq);
		id.setSeqp(seqp);
		mamTrgGerais.setId(id);
	}

	/**
	 * Regras para botão Gravar (Obs.: Sempre que esse botão for clicado deve-se inserir um novo registro em MAM_TRG_GRAVIDADES.
	 * 
	 * RN03 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @ORADB MAM_TRG_GRAVIDADES.MAM_TGG_BRI
	 * 
	 * @param item
	 * @throws ApplicationBusinessException
	 */
	public void preInsert(MamTrgGravidade mamTrgGravidade, String micNome) throws ApplicationBusinessException {
		// 1. Seta no campo CRIADO_EM a data atual
		mamTrgGravidade.setCriadoEm(new Date());	
		
		mamTrgGravidade.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		
		// 4. Seta no campo MIC_NOME o nome do microcomputador conectado
		mamTrgGravidade.setMicNome(micNome);
	}

	// private
}
