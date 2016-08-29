package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.model.MpmEvolucoes;
import br.gov.mec.aghu.model.MpmNotaAdicionalEvolucoes;
import br.gov.mec.aghu.prescricaoenfermagem.vo.RelatorioEvolucoesNotasAdicionaisPacienteVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.RelatorioEvolucoesPacienteVO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAnamnesesDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmEvolucoesDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmNotaAdicionalEvolucoesDAO;

@Stateless
public class RelatorioEvolucoesPacienteON extends BaseBusiness {

	private static final long serialVersionUID = 976377475017607434L;

	private static final Log LOG = LogFactory
			.getLog(RelatorioEvolucoesPacienteON.class);

	@Inject
	private MpmEvolucoesDAO mpmEvolucoesDAO;

	@Inject
	private MpmAnamnesesDAO mpmAnamnesesDAO;

	@Inject
	private MpmNotaAdicionalEvolucoesDAO mpmNotaAdicionalEvolucoesDAO;

	@EJB
	private IAghuFacade aghuFacade;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public List<RelatorioEvolucoesPacienteVO> gerarRelatorioEvolucaoPaciente(
			Long seqEvolucao) {
		List<RelatorioEvolucoesPacienteVO> relatorio = new ArrayList<RelatorioEvolucoesPacienteVO>();
		RelatorioEvolucoesPacienteVO relatorioEvolucoes = new RelatorioEvolucoesPacienteVO();
		MpmEvolucoes evolucao = getMpmEvolucoesDAO().obterPorChavePrimaria(
				seqEvolucao);
		// Dados do paciente
		relatorioEvolucoes.setNomeMaePaciente(evolucao.getAnamnese()
				.getAtendimento().getPaciente().getNomeMae());
		relatorioEvolucoes.setNomePaciente(evolucao.getAnamnese()
				.getAtendimento().getPaciente().getNome());
		relatorioEvolucoes.setIdade(evolucao.getAnamnese().getAtendimento()
				.getPaciente().getIdadeFormat());
		relatorioEvolucoes.setProntuario(evolucao.getAnamnese()
				.getAtendimento().getPaciente().getProntuario().toString());
		relatorioEvolucoes.setUnidade(evolucao.getAnamnese().getAtendimento()
				.getUnidadeFuncional().getDescricao());
		relatorioEvolucoes.setEspecialidade(evolucao.getAnamnese()
				.getAtendimento().getEspecialidade().getNomeEspecialidade());
		relatorioEvolucoes.setReferencia(evolucao.getDthrReferencia());

		// Evolucao
		relatorioEvolucoes
				.setEvolucoes(new ArrayList<RelatorioEvolucoesNotasAdicionaisPacienteVO>());
		relatorioEvolucoes.getEvolucoes().add(
				obtemDadosEvolucoesNotasAdicionais(evolucao));
		relatorio.add(relatorioEvolucoes);
		return relatorio;
	}

	private RelatorioEvolucoesNotasAdicionaisPacienteVO obtemDadosEvolucoesNotasAdicionais(
			MpmEvolucoes evolucao) {
		RelatorioEvolucoesNotasAdicionaisPacienteVO dadosEvolucao = new RelatorioEvolucoesNotasAdicionaisPacienteVO();
		dadosEvolucao.setDataCriacao(evolucao.getDthrCriacao());
		dadosEvolucao.setDataFim(evolucao.getDthrFim());
		dadosEvolucao.setDataReferencia(evolucao.getDthrReferencia());
		dadosEvolucao.setDescricao(evolucao.getDescricao());
		dadosEvolucao.setNomeResponsavel(evolucao.getServidor()
				.getPessoaFisica().getNome());
		dadosEvolucao.setAtendimento(evolucao.getAnamnese().getAtendimento()
				.getSeq().toString());

		List<MpmNotaAdicionalEvolucoes> notasAdicionais = getMpmNotaAdicionalEvolucoesDAO()
				.listarNotasAdicionaisEvolucoesPorSeqEvolucao(evolucao.getSeq());
		if (!notasAdicionais.isEmpty()) {
			StringBuilder textNotasAdicionais = new StringBuilder();
			for (MpmNotaAdicionalEvolucoes notaAdicional : notasAdicionais) {
				textNotasAdicionais.append(notaAdicional.getDescricao());
				textNotasAdicionais.append('\n');
			}
			dadosEvolucao.setNotasAdicionais(textNotasAdicionais.toString());
		}
		return dadosEvolucao;
	}

	protected MpmEvolucoesDAO getMpmEvolucoesDAO() {
		return mpmEvolucoesDAO;
	}

	protected MpmAnamnesesDAO getMpmAnamnesesDAO() {
		return mpmAnamnesesDAO;
	}

	protected MpmNotaAdicionalEvolucoesDAO getMpmNotaAdicionalEvolucoesDAO() {
		return mpmNotaAdicionalEvolucoesDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

}