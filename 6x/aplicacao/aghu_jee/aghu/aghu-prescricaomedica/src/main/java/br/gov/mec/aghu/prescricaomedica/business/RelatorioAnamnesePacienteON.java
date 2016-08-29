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
import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.MpmNotaAdicionalAnamneses;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAnamnesesDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmEvolucoesDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmNotaAdicionalAnamnesesDAO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioAnamneseNotasAdicionaisPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioAnamnesePacienteVO;

@Stateless
public class RelatorioAnamnesePacienteON extends BaseBusiness{

	private static final long serialVersionUID = 976377475017607434L;
	
	private static final Log LOG = LogFactory
			.getLog(RelatorioAnamnesePacienteON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private MpmNotaAdicionalAnamnesesDAO mpmNotaAdicionalAnamnesesDAO;
	
	@Inject
	private MpmAnamnesesDAO mpmAnamnesesDAO;
	
	@Inject
	private MpmEvolucoesDAO mpmEvolucoesDAO;

	public List<RelatorioAnamnesePacienteVO> gerarRelatorioAnamnesePaciente(
			Long anaSeq) {
		MpmAnamneses anamnese = getMpmAnamnesesDAO()
				.obterAnamneseValidadaPorAnamneses(anaSeq);
		List<RelatorioAnamnesePacienteVO> relatorio = new ArrayList<RelatorioAnamnesePacienteVO>();
		RelatorioAnamnesePacienteVO relatorioAnamnese = new RelatorioAnamnesePacienteVO();
		relatorioAnamnese.setNomePaciente(anamnese.getAtendimento()
				.getPaciente().getNome());
		relatorioAnamnese.setIdade(anamnese.getAtendimento().getPaciente()
				.getIdadeFormat());
		relatorioAnamnese.setProntuario(anamnese.getAtendimento().getPaciente()
				.getProntuario().toString());
		relatorioAnamnese.setUnidade(anamnese.getAtendimento()
				.getUnidadeFuncional().getDescricao());
		relatorioAnamnese.setEspecialidade(anamnese.getAtendimento()
				.getEspecialidade().getNomeEspecialidade());
		relatorioAnamnese.setReferencia(anamnese.getDthrAlteracao());

		List<RelatorioAnamneseNotasAdicionaisPacienteVO> anamneseNotasAdicionaisList = new ArrayList<RelatorioAnamneseNotasAdicionaisPacienteVO>();
		RelatorioAnamneseNotasAdicionaisPacienteVO anamneseNotasAdicionais = new RelatorioAnamneseNotasAdicionaisPacienteVO();
		anamneseNotasAdicionais.setDataConfirmacao(anamnese.getDthrAlteracao());
		anamneseNotasAdicionais.setDescricao(anamnese.getDescricao());
		anamneseNotasAdicionais.setNomeResponsavel(anamnese.getServidor()
				.getPessoaFisica().getNome());
		anamneseNotasAdicionais.setAtendimento(anamnese.getAtendimento()
				.getSeq().toString());
		anamneseNotasAdicionais.setDataCriacao(anamnese.getDthrCriacao());

		// Notas adicionais
		List<MpmNotaAdicionalAnamneses> notasAdicionais = getMpmNotaAdicionalAnamnesesDAO()
				.listarNotasAdicionaisAnamnesesPorSeqAnamneses(anaSeq);
		if (!notasAdicionais.isEmpty()) {
			StringBuilder textNotasAdicionais = new StringBuilder();
			for (MpmNotaAdicionalAnamneses notaAdicional : notasAdicionais) {
				textNotasAdicionais.append(notaAdicional.getDescricao());
				textNotasAdicionais.append(" \n ");
			}
			anamneseNotasAdicionais.setNotasAdicionais(textNotasAdicionais
					.toString());
		}

		anamneseNotasAdicionaisList.add(anamneseNotasAdicionais);
		relatorioAnamnese
				.setAnamneseNotasAdicionais(anamneseNotasAdicionaisList);
		relatorio.add(relatorioAnamnese);
		return relatorio;
	}

	protected MpmEvolucoesDAO getMpmEvolucoesDAO() {
		return mpmEvolucoesDAO;
	}

	protected MpmAnamnesesDAO getMpmAnamnesesDAO() {
		return mpmAnamnesesDAO;
	}

	protected MpmNotaAdicionalAnamnesesDAO getMpmNotaAdicionalAnamnesesDAO() {
		return mpmNotaAdicionalAnamnesesDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

}