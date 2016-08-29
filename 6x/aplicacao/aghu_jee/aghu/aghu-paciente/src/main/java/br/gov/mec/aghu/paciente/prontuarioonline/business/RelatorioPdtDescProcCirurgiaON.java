package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.dominio.DominioProcedimentoTerapeuticoOperatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricao;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.model.PdtDadoDesc;
import br.gov.mec.aghu.model.PdtDadoImg;
import br.gov.mec.aghu.model.PdtDescObjetiva;
import br.gov.mec.aghu.model.PdtDescTecnica;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.PdtEquipamento;
import br.gov.mec.aghu.model.PdtImg;
import br.gov.mec.aghu.model.PdtMedicDesc;
import br.gov.mec.aghu.model.PdtNotaAdicional;
import br.gov.mec.aghu.model.PdtProc;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.model.PdtProf;
import br.gov.mec.aghu.model.PdtSolicTemp;
import br.gov.mec.aghu.model.PdtTecnica;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.PdtDescricaoProcedimentoCirurgiaVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity"})
@Stateless
public class RelatorioPdtDescProcCirurgiaON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(RelatorioPdtDescProcCirurgiaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	private static final long serialVersionUID = -5044966756575908140L;	

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<PdtDescricaoProcedimentoCirurgiaVO> pesquisarRelatorioPdtDescricaoProcedimentoCirurgia(
			Integer seqPdtDescricao) throws ApplicationBusinessException  {

		final IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade = getBlocoCirurgicoProcDiagTerapFacade();
		PdtDescricaoProcedimentoCirurgiaVO voRelatorio = new PdtDescricaoProcedimentoCirurgiaVO();
		Enum[] fetchArgsInnerJoin = { PdtDescricao.Fields.SERVIDOR};
		PdtDescricao pdtDescricao = getBlocoCirurgicoProcDiagTerapFacade().obterPdtDescricao(seqPdtDescricao, fetchArgsInnerJoin, null);	

		voRelatorio.setListaC03(processarC03(pdtDescricao.getSeq())); // f11,// f12,// f13
		voRelatorio.setListaC09C10(processarC09C10(pdtDescricao.getSeq())); // C9(f23,// f29)// e//C10(f06)
		voRelatorio.setListaC15(processarC15(pdtDescricao.getSeq())); // C15(f17,// f19,// f27)
		voRelatorio.setListaC22C24RN8(processarC22C24RN8(pdtDescricao.getSeq())); // C22(f35,// f37),// C24(f34),// RN8(f33)
		voRelatorio.setListaC26C27(processarC26C27(pdtDescricao.getSeq()));// C26(f42),// C27(f43)
		voRelatorio.setListaC29(processarC29(pdtDescricao.getSeq(),	pdtDescricao.getServidor().getId().getMatricula()));// C29 (f44)

		// C25 não implementada, em razão do resultado "MAX_SEQP" ser igual ao
		// resultado da C21(notaAdicional.get(0).getId().getSeqp()).
		// Desta forma a passagem do valor para RN6/RN14/RN22 (max_Seqp/C25)
		// continua preservada através da C21.
		// C21: seqPdtDescricao = parâmetro passado pela estória 15825
		List<PdtNotaAdicional> notaAdicional = blocoCirurgicoProcDiagTerapFacade.pesquisarNotaAdicionalPorDdtSeqESeqpDesc(seqPdtDescricao);// C21
		if (notaAdicional != null && !notaAdicional.isEmpty()) {
			voRelatorio.setF07(processarRN6(notaAdicional.get(0).getId().getSeqp()));
			voRelatorio.setNotaAdicional(false);
			if (processarRN14(notaAdicional.get(0).getId().getSeqp())) {
				//voRelatorio.setNumero(processarRN22(notaAdicional.get(0).getId().getSeqp()));
				voRelatorio.setNotaAdicional(true);
				voRelatorio.setListaC19C21C25(processarC19C21C25(pdtDescricao
						.getSeq(), notaAdicional.get(0).getCriadoEm())); // C19(f18/f31),// C21(f41),// C25(RN14/RN22)
				
				voRelatorio.setF40(voRelatorio.getListaC19C21C25().get(voRelatorio.getListaC19C21C25().size()-1).getTexto3());
				voRelatorio.setF41(voRelatorio.getListaC19C21C25().get(voRelatorio.getListaC19C21C25().size()-1).getTexto4());
			}
		}

		MbcCirurgias cirurgia = getBlocoCirurgicoFacade().obterCirurgiaPorSeq(pdtDescricao.getMbcCirurgias().getSeq());// C2
		if (cirurgia != null) {
			voRelatorio.setfNome(cirurgia.getPaciente().getNome());// fNome				
			voRelatorio.setfProntuario(CoreUtil.formataProntuario(cirurgia.getPaciente().getProntuario()));// fProntuario
			voRelatorio.setfIdade(getProntuarioOnlineFacade().buscarIdade(
					cirurgia.getPaciente().getDtNascimento(), cirurgia.getData()).replace(" 0 mês", ""));// fIdade
			voRelatorio.setfConvenio(cirurgia.getConvenioSaude().getDescricao());// fConvenio
			voRelatorio.setfPaciente(cirurgia.getPaciente().getNome());// fPaciente
			voRelatorio.setF02(CoreUtil.formataProntuario(cirurgia.getPaciente().getProntuario()));// fProntuario
			voRelatorio.setfPacCo(cirurgia.getPaciente().getCodigo());// fPacCo			
			voRelatorio.setfLeito(processarRN12(cirurgia));
			if (cirurgia.getSalaCirurgica() != null){
				voRelatorio.setF01(cirurgia.getSalaCirurgica().getUnidadeFuncional().getDescricao());// f01
			}				
			if (cirurgia.getPaciente().getSexo() != null){
				voRelatorio.setfSexo(cirurgia.getPaciente().getSexo().getDescricao());// fSexo
			}
			if(cirurgia.getAtendimento() != null && cirurgia.getAtendimento().getLeito() != null ){
				voRelatorio.setF10(cirurgia.getAtendimento().getLeito().getLeitoID());//f10
			}
		}

		PdtSolicTemp solicTemp = getBlocoCirurgicoProcDiagTerapFacade().obterSolicTempPorDdtSeq(pdtDescricao.getSeq());// C4
		if (solicTemp != null) {
			voRelatorio.setfMotivo(solicTemp.getMotivo());// fMotivo	
			String nome = null;
			if (solicTemp.getServidor() != null) {				
				RapPessoasFisicas pessoa = solicTemp.getServidor().getPessoaFisica();
				if (pessoa != null) {
					nome = pessoa.getNome();
				} 
			}
			if (processarRN19(nome)) {
				voRelatorio.setfSolicitadoPor(solicTemp.getSolicitadoPor());// fSolicitadoPor
			}
			if (processarRN21(solicTemp.getObservacoes())) {
				voRelatorio.setFddtComplemento(solicTemp.getObservacoes());// fddtComplemento
			}
		}

		PdtDadoDesc dadoDesc = blocoCirurgicoProcDiagTerapFacade.obterDadoDescPorDdtSeq(pdtDescricao.getSeq());// C5
		if (dadoDesc != null) {
			voRelatorio.setF20(DateUtil.dataToString(dadoDesc.getDthrInicio(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));// f20
			voRelatorio.setF21(DateUtil.dataToString(dadoDesc.getDthrFim(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));// f21
			voRelatorio.setF22(dadoDesc.getCarater().getDescricao());// f22
			voRelatorio.setF09(dadoDesc.getSedacao()?"com Sedação":"sem Sedação");// f09
			if (processarRN15(dadoDesc.getNroFilme())) {
				voRelatorio.setF16(dadoDesc.getNroFilme().toString());// f16
			}
			if (processarRN20(dadoDesc.getObservacoesProc())) {
				voRelatorio.setF25(dadoDesc.getObservacoesProc());// f25
			}
			if(dadoDesc.getTanSeq() != null){
				MbcTipoAnestesias tipoAnestesias = getBlocoCirurgicoFacade().obterMbcTipoAnestesiaPorChavePrimaria(dadoDesc.getTanSeq());// C6
				if (tipoAnestesias != null) {
					voRelatorio.setF08(tipoAnestesias.getDescricao()); // f08
				}
			}
			PdtEquipamento equipamento = dadoDesc.getDeqSeq() != null ? blocoCirurgicoProcDiagTerapFacade.obterEquipamentoPorChavePrimaria(
					dadoDesc.getDeqSeq()) : null; // C7
			if (equipamento != null) {
				voRelatorio.setF14(equipamento.getDescricao());// f14
			}
			PdtTecnica tecnica = dadoDesc.getDteSeq() != null ? getBlocoCirurgicoProcDiagTerapFacade().obterTecnicaPorChavePrimaria(dadoDesc.getDteSeq()) :null; // C8
			if (tecnica != null) {
				voRelatorio.setF15(tecnica.getDescricao());// f15
			}
		}

		PdtDescTecnica pdtDescTecnica = blocoCirurgicoProcDiagTerapFacade.obterDescricaoTecnicaPorDdtSeq(pdtDescricao.getSeq());// C11
		if (pdtDescTecnica != null) {
			voRelatorio.setF26(pdtDescTecnica.getDescricao());// f26
		}

		AghEspecialidades especialidade = pdtDescricao.getEspecialidade();// C30
		if (especialidade != null) {
			voRelatorio.setF03(especialidade.getNomeEspecialidade()); // f03
		}

		MbcCirurgias mbcCirurgia = getBlocoCirurgicoFacade().obterCirurgiaProjetpPesquisa(pdtDescricao.getMbcCirurgias().getSeq());// C31
		if (mbcCirurgia != null) {
			StringBuffer pjqNomeProj = new StringBuffer();
			//Defeito 21148
			//pjqNomeProj.append(mbcCirurgia.getProjetoPesquisa().getNumero()).append(" - ").append(mbcCirurgia.getProjetoPesquisa().getNome());
			pjqNomeProj.append(mbcCirurgia.getProjetoPesquisa().getNome());
			if (processarRN17(pjqNomeProj.toString())) {
				voRelatorio.setF24(pjqNomeProj.toString());// f24
			}
			if (processarRN16(mbcCirurgia.getProjetoPesquisa().getNomeResponsavel())) {
				voRelatorio.setF28(mbcCirurgia.getProjetoPesquisa().getNomeResponsavel());// f28
			}
		}

		voRelatorio.setF30(processarRN5(pdtDescricao));
		voRelatorio.setF32(seqPdtDescricao);
		voRelatorio.setF38(DateUtil.dataToString(pdtDescricao.getDthrConclusao() != null ? pdtDescricao.getDthrConclusao()
						: pdtDescricao.getCriadoEm(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		voRelatorio.setF39(processarRn9Rn11(pdtDescricao.getServidor().getId().getMatricula(), 
				pdtDescricao.getServidor().getId().getVinCodigo()));
		voRelatorio.setfCurrentDate(DateUtil.dataToString(new Date(), "dd/MM/yyyy HH:mm:ss"));

		return Arrays.asList(voRelatorio);
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<PdtDescricaoProcedimentoCirurgiaVO> pesquisarRelatorioPdtDescricaoProcedimentoCirurgiaPorCrgSeq(
			Integer crgSeq) throws ApplicationBusinessException  {
		
		List<PdtDescricao> listaPdtDescricaos = getBlocoCirurgicoProcDiagTerapFacade().listarDescricaoPorSeqCirurgia(crgSeq);
		List<PdtDescricaoProcedimentoCirurgiaVO> listaPdtDescricaoProcedimentoCirurgiaVO = new ArrayList<PdtDescricaoProcedimentoCirurgiaVO>();
		int i = 0;
		final IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade = getBlocoCirurgicoProcDiagTerapFacade();
		
		for(PdtDescricao pdtDescricaoPojo : listaPdtDescricaos){
			
			PdtDescricaoProcedimentoCirurgiaVO voRelatorio = new PdtDescricaoProcedimentoCirurgiaVO();
			
			//PdtDescricao pdtDescricao = getBlocoCirurgicoProcDiagTerapFacade().obterPdtDescricao(pdtDescricaoPojo.getSeq());// C1		
			voRelatorio.setCountRegistros(i++);
			voRelatorio.setListaC03(processarC03(pdtDescricaoPojo.getSeq())); // f11,// f12,// f13
			voRelatorio.setListaC09C10(processarC09C10(pdtDescricaoPojo.getSeq())); // C9(f23,// f29)// e//C10(f06)
			voRelatorio.setListaC15(processarC15(pdtDescricaoPojo.getSeq())); // C15(f17,// f19,// f27)
			voRelatorio.setListaC22C24RN8(processarC22C24RN8(pdtDescricaoPojo.getSeq())); // C22(f35,// f37),// C24(f34),// RN8(f33)
			voRelatorio.setListaC26C27(processarC26C27(pdtDescricaoPojo.getSeq()));// C26(f42),// C27(f43)
			voRelatorio.setListaC29(processarC29(pdtDescricaoPojo.getSeq(),	pdtDescricaoPojo.getServidor().getId().getMatricula()));// C29 (f44)
	
			// C25 não implementada, em razão do resultado "MAX_SEQP" ser igual ao
			// resultado da C21(notaAdicional.get(0).getId().getSeqp()).
			// Desta forma a passagem do valor para RN6/RN14/RN22 (max_Seqp/C25)
			// continua preservada através da C21.
			// C21: seqpdtDescricaoPojo = parâmetro passado pela estória 15825
			List<PdtNotaAdicional> notaAdicional = blocoCirurgicoProcDiagTerapFacade.pesquisarNotaAdicionalPorDdtSeqESeqpDesc(pdtDescricaoPojo.getSeq());// C21
			if (notaAdicional != null && !notaAdicional.isEmpty()) {
				voRelatorio.setF07(processarRN6(notaAdicional.get(0).getId().getSeqp()));
				voRelatorio.setNotaAdicional(false);
				if (processarRN14(notaAdicional.get(0).getId().getSeqp())) {
					//voRelatorio.setNumero(processarRN22(notaAdicional.get(0).getId().getSeqp()));
					voRelatorio.setNotaAdicional(true);
					voRelatorio.setListaC19C21C25(processarC19C21C25(pdtDescricaoPojo
							.getSeq(), notaAdicional.get(0).getCriadoEm())); // C19(f18/f31),// C21(f41),// C25(RN14/RN22)
					
					voRelatorio.setF40(voRelatorio.getListaC19C21C25().get(voRelatorio.getListaC19C21C25().size()-1).getTexto3());
					voRelatorio.setF41(voRelatorio.getListaC19C21C25().get(voRelatorio.getListaC19C21C25().size()-1).getTexto4());
				}
			}
	
			MbcCirurgias cirurgia = getBlocoCirurgicoFacade().obterCirurgiaPorSeq(pdtDescricaoPojo.getMbcCirurgias().getSeq());// C2
			if (cirurgia != null) {
				voRelatorio.setfNome(cirurgia.getPaciente().getNome());// fNome				
				voRelatorio.setfProntuario(CoreUtil.formataProntuario(cirurgia.getPaciente().getProntuario()));// fProntuario
				voRelatorio.setfIdade(getProntuarioOnlineFacade().buscarIdade(
						cirurgia.getPaciente().getDtNascimento(), cirurgia.getData()).replace(" 0 mês", ""));// fIdade
				voRelatorio.setfConvenio(cirurgia.getConvenioSaude().getDescricao());// fConvenio
				voRelatorio.setfPaciente(cirurgia.getPaciente().getNome());// fPaciente
				voRelatorio.setF02(CoreUtil.formataProntuario(cirurgia.getPaciente().getProntuario()));// fProntuario
				voRelatorio.setfPacCo(cirurgia.getPaciente().getCodigo());// fPacCo
				if(cirurgia.getAtendimento() != null){
					voRelatorio.setfLeito(processarRN12(cirurgia));// fLeito
				}	
				if (cirurgia.getSalaCirurgica() != null){
					voRelatorio.setF01(cirurgia.getSalaCirurgica().getUnidadeFuncional().getDescricao());// f01
				}				
				if (cirurgia.getPaciente().getSexo() != null){
					voRelatorio.setfSexo(cirurgia.getPaciente().getSexo().getDescricao());// fSexo
				}
				if(cirurgia.getAtendimento()!= null && cirurgia.getAtendimento().getLeito() != null ){
					voRelatorio.setF10(cirurgia.getAtendimento().getLeito().getLeitoID());//f10
				}
			}
	
			PdtSolicTemp solicTemp = getBlocoCirurgicoProcDiagTerapFacade().obterSolicTempPorDdtSeq(pdtDescricaoPojo.getSeq());// C4
			if (solicTemp != null) {
				voRelatorio.setfMotivo(solicTemp.getMotivo());// fMotivo	
				String nome = null;
				if (solicTemp.getServidor() != null) {				
					RapPessoasFisicas pessoa = solicTemp.getServidor().getPessoaFisica();
					if (pessoa != null) {
						nome = pessoa.getNome();
					} 
				}
				if (processarRN19(nome)) {
					voRelatorio.setfSolicitadoPor(solicTemp.getSolicitadoPor());// fSolicitadoPor
				}
				if (processarRN21(solicTemp.getObservacoes())) {
					voRelatorio.setFddtComplemento(solicTemp.getObservacoes());// fddtComplemento
				}
			}
	
			PdtDadoDesc dadoDesc = blocoCirurgicoProcDiagTerapFacade.obterDadoDescPorDdtSeq(pdtDescricaoPojo.getSeq());// C5
			if (dadoDesc != null) {
				voRelatorio.setF20(DateUtil.dataToString(dadoDesc.getDthrInicio(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));// f20
				voRelatorio.setF21(DateUtil.dataToString(dadoDesc.getDthrFim(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));// f21
				voRelatorio.setF22(dadoDesc.getCarater().getDescricao());// f22
				voRelatorio.setF09(dadoDesc.getSedacao()?"com Sedação":"sem Sedação");// f09
				if (processarRN15(dadoDesc.getNroFilme())) {
					voRelatorio.setF16(dadoDesc.getNroFilme().toString());// f16
				}
				if (processarRN20(dadoDesc.getObservacoesProc())) {
					voRelatorio.setF25(dadoDesc.getObservacoesProc());// f25
				}
				if(dadoDesc.getTanSeq() != null){
					MbcTipoAnestesias tipoAnestesias = getBlocoCirurgicoFacade().obterMbcTipoAnestesiaPorChavePrimaria(dadoDesc.getTanSeq());// C6
					if (tipoAnestesias != null) {
						voRelatorio.setF08(tipoAnestesias.getDescricao()); // f08
					}
				}
				PdtEquipamento equipamento = dadoDesc.getDeqSeq() != null ? blocoCirurgicoProcDiagTerapFacade.obterEquipamentoPorChavePrimaria(
						dadoDesc.getDeqSeq()) : null; // C7
				if (equipamento != null) {
					voRelatorio.setF14(equipamento.getDescricao());// f14
				}
				PdtTecnica tecnica = dadoDesc.getDteSeq() != null ? getBlocoCirurgicoProcDiagTerapFacade().obterTecnicaPorChavePrimaria(dadoDesc.getDteSeq()) :null; // C8
				if (tecnica != null) {
					voRelatorio.setF15(tecnica.getDescricao());// f15
				}
			}
	
			PdtDescTecnica pdtDescTecnica = blocoCirurgicoProcDiagTerapFacade.obterDescricaoTecnicaPorDdtSeq(pdtDescricaoPojo.getSeq());// C11
			if (pdtDescTecnica != null) {
				voRelatorio.setF26(pdtDescTecnica.getDescricao());// f26
			}
	
			AghEspecialidades especialidade = pdtDescricaoPojo.getEspecialidade();// C30
			if (especialidade != null) {
				voRelatorio.setF03(especialidade.getNomeEspecialidade()); // f03
			}
	
			MbcCirurgias mbcCirurgia = getBlocoCirurgicoFacade().obterCirurgiaProjetpPesquisa(pdtDescricaoPojo.getMbcCirurgias().getSeq());// C31
			if (mbcCirurgia != null) {
				StringBuffer pjqNomeProj = new StringBuffer();
				//Defeito 21148
				//pjqNomeProj.append(mbcCirurgia.getProjetoPesquisa().getNumero()).append(" - ").append(mbcCirurgia.getProjetoPesquisa().getNome());
				pjqNomeProj.append(mbcCirurgia.getProjetoPesquisa().getNome());
				if (processarRN17(pjqNomeProj.toString())) {
					voRelatorio.setF24(pjqNomeProj.toString());// f24
				}
				if (processarRN16(mbcCirurgia.getProjetoPesquisa().getNomeResponsavel())) {
					voRelatorio.setF28(mbcCirurgia.getProjetoPesquisa().getNomeResponsavel());// f28
				}
			}
	
			voRelatorio.setF30(processarRN5(pdtDescricaoPojo));
			voRelatorio.setF32(pdtDescricaoPojo.getSeq());
			voRelatorio.setF38(DateUtil.dataToString(pdtDescricaoPojo.getDthrConclusao() != null ? pdtDescricaoPojo.getDthrConclusao()
							: pdtDescricaoPojo.getCriadoEm(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			voRelatorio.setF39(processarRn9Rn11(pdtDescricaoPojo.getServidor().getId().getMatricula(), 
					pdtDescricaoPojo.getServidor().getId().getVinCodigo()));
			voRelatorio.setfCurrentDate(DateUtil.dataToString(new Date(), "dd/MM/yyyy HH:mm:ss"));
			
			listaPdtDescricaoProcedimentoCirurgiaVO.add(voRelatorio);
		}	

		return listaPdtDescricaoProcedimentoCirurgiaVO;
	}
	
	private List<LinhaReportVO> processarC03(Integer ddtSeq) throws ApplicationBusinessException {
		List<PdtProf> listaProfissao = getBlocoCirurgicoProcDiagTerapFacade().pesquisarProfPorDdtSeq(ddtSeq); // C3		 
		CoreUtil.ordenarLista(listaProfissao, "tipoAtuacao.codigo", true);		
		
		List<LinhaReportVO> c03 = new ArrayList<LinhaReportVO>(); // f11, f12,// f13
		for (PdtProf prof : listaProfissao) {
			LinhaReportVO listaC03 = new LinhaReportVO();
			// f11
			listaC03.setTexto1(prof.getTipoAtuacao() != null ? prof.getTipoAtuacao().getDescricaoPdtProf() + ":" : null);
			// f12: RESTRICAO VINDA DA FUNCAO RAPC_BUSCA_NOME
			String dpfNome2;			
			if (prof.getServidorPrf() != null) {
				RapPessoasFisicas pessoa = obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(
						prof.getServidorPrf().getId().getMatricula(), prof.getServidorPrf().getId().getVinCodigo());
				if (pessoa != null) {
					dpfNome2 = pessoa.getNome();
				} else {
					dpfNome2 = "";
				}
			} else if(prof.getNome() != null){
				dpfNome2 = prof.getNome(); 
			} else {
				dpfNome2 = "";
			}
			listaC03.setTexto2(dpfNome2);
			// f13
			String dpfCrm = null;
			if(prof.getServidorPrf() != null){
				dpfCrm = getRegistroColaboradorFacade().buscarNroRegistroConselho(prof.getServidorPrf().getId().getVinCodigo(), prof.getServidorPrf().getId().getMatricula());
			}
			StringBuffer dpfCrm2 = new StringBuffer();
			if (dpfCrm == null) {
				dpfCrm2.append("");
			} else {
				String conselho = "";
				BuscaConselhoProfissionalServidorVO conselhoProfissionalServidor = getPrescricaoMedicaFacade()
						.buscaConselhoProfissionalServidorVO(prof.getServidorPrf().getId().getMatricula(), prof.getServidorPrf().getId().getVinCodigo());
				if(conselhoProfissionalServidor != null && conselhoProfissionalServidor.getNome()!= null){
					conselho = conselhoProfissionalServidor.getSiglaConselho();
				}
				dpfCrm2.append(conselho).append(": ").append(dpfCrm);
			}
			listaC03.setTexto3(dpfCrm2.toString());
			c03.add(listaC03);
		}
		return c03;
	}

	private List<LinhaReportVO> processarC09C10(Integer seq) {
		List<PdtProc> listaProc = getBlocoCirurgicoProcDiagTerapFacade().pesquisarPdtProcPorDdtSeq(seq);// C9
		List<LinhaReportVO> listaC09C10 = new ArrayList<LinhaReportVO>();// f06,// f23,// f29
		for (PdtProc proc : listaProc) {
			LinhaReportVO linhaReport = new LinhaReportVO();
			PdtProcDiagTerap procDiagTerap = getBlocoCirurgicoProcDiagTerapFacade()
					.obterPdtProcDiagTerap(proc.getPdtProcDiagTerap().getSeq());// C10
			if (procDiagTerap != null) {
				linhaReport.setTexto1(procDiagTerap.getDescricao() != null ? procDiagTerap.getDescricao() : null);// f06
			}
			linhaReport.setTexto2(proc.getComplemento() != null ? proc.getComplemento() : null);// f23
			linhaReport.setTexto3(proc.getIndContaminacao().getDescricao() != null ? proc
							.getIndContaminacao().getDescricao() : null);// f29
			listaC09C10.add(linhaReport);
		}
		return listaC09C10;
	}

	private List<LinhaReportVO> processarC15(Integer seq) {
		List<PdtDescObjetiva> listaDescObjetiva = getBlocoCirurgicoProcDiagTerapFacade().pesquisarPdtDescObjetivaPorDdtSeq(seq);// C15
		List<LinhaReportVO> listaC15 = new ArrayList<LinhaReportVO>();// f27,// f19,// f17
		for (PdtDescObjetiva descObjetiva : listaDescObjetiva) {
			LinhaReportVO linhaReport = new LinhaReportVO();
			linhaReport.setTexto1(descObjetiva.getPdtAchado().getPdtGrupo()
					.getDescricao() != null ? descObjetiva.getPdtAchado().getPdtGrupo().getDescricao() : null);// f27
			linhaReport.setTexto2(descObjetiva.getPdtAchado().getDescricao() != null ? descObjetiva.getPdtAchado().getDescricao() : null);// f19
			linhaReport.setTexto3(descObjetiva.getComplemento() != null ? descObjetiva.getComplemento() : null);// f17
			listaC15.add(linhaReport);
		}
		return listaC15;
	}

	private List<LinhaReportVO> processarC19C21C25(Integer ddtSeq, Date criadoEm) throws ApplicationBusinessException {
		List<PdtNotaAdicional> listaNotaAdicional = getBlocoCirurgicoProcDiagTerapFacade().pesquisarNotaAdicionalPorDdtSeq(ddtSeq); // C19
		List<LinhaReportVO> listaC19C21C25 = new ArrayList<LinhaReportVO>();// f18,// f31
		for (PdtNotaAdicional notaAdic : listaNotaAdicional) {
			LinhaReportVO linhaReport = new LinhaReportVO();
			// f18
			linhaReport.setTexto1(notaAdic.getNotaAdicional() != null ? notaAdic.getNotaAdicional() : null);
			// f31
			linhaReport.setTexto2(getAmbulatorioFacade().mpmcMinusculo(getPrescricaoMedicaFacade().obtemNomeServidorEditado(
						notaAdic.getRapServidores().getId().getVinCodigo(), notaAdic.getRapServidores().getId().getMatricula()), 2));
			if (processarRN18(notaAdic.getRapServidores().getId().getMatricula())) {
				// f40
				linhaReport.setTexto3(processarRn9Rn11(notaAdic.getRapServidores().getId().getMatricula(), notaAdic
						.getRapServidores().getId().getVinCodigo()));
				// f41
				linhaReport.setTexto4(DateUtil.dataToString(criadoEm, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			}
			listaC19C21C25.add(linhaReport);
		}
		return listaC19C21C25;
	}

	private List<LinhaReportVO> processarC22C24RN8(Integer seq) {
		List<PdtMedicDesc> listaMedicDesc = getBlocoCirurgicoProcDiagTerapFacade().pesquisarMedicDescPorDdtSeq(seq);// C22
		List<LinhaReportVO> listaC22C24RN8 = new ArrayList<LinhaReportVO>();// f33,// f34,// f35,// f37
		for (PdtMedicDesc medicDesc : listaMedicDesc) {
			LinhaReportVO linhaReport = new LinhaReportVO();
			// DMD_PROMPT para RN8 
			// (DECODE(PRE_TRANS,'PRE','Medicação // pré-procedimento :', PRE_TRANS,'Medicação trans-procedimento:',null))
			// obs: PRE_TRANS definido como Not Null em PdtMedicDesc			
			linhaReport.setTexto1(processarRN8(DominioProcedimentoTerapeuticoOperatorio.PRE.equals(medicDesc.getPreTrans()) ? "Medicação pré-procedimento   :"
							: "Medicação trans-procedimento :"));// f33
			
			// substituida consulta na VAfaMdtoDescricao pelo método da
			// AfaMedicamento que reproduz a mesma forma de editar a descricao
			AfaMedicamento medicamento = getFarmaciaFacade().obterMedicamento(medicDesc.getAfaMedicamento().getMatCodigo());// C24
			if (medicamento != null) {
				linhaReport.setTexto2(medicamento.getDescricaoEditada() != null ? medicamento.getDescricaoEditada() : null);// f34
			}
			linhaReport.setTexto3(medicDesc.getDose() != null ? medicDesc.getDose().toString() : null);// f35
			linhaReport.setTexto4(medicDesc.getUnidade() != null ? medicDesc.getUnidade() : null);// f37
			listaC22C24RN8.add(linhaReport);
		}
		return listaC22C24RN8;
	}

	private List<LinhaReportVO> processarC26C27(Integer ddtSeq) {
		List<PdtDadoImg> listaDadoImg = getBlocoCirurgicoProcDiagTerapFacade().pesquisarPdtDadoImgPorDdtSeq(ddtSeq);// C26		
		if (listaDadoImg != null && !listaDadoImg.isEmpty()){
			List<LinhaReportVO> listaC26C27 = new ArrayList<LinhaReportVO>();// f42,// f43
			for (PdtDadoImg dadoImg : listaDadoImg) {
				LinhaReportVO linhaReport = new LinhaReportVO();	
				linhaReport.setTexto1(dadoImg.getTexto() != null ? dadoImg.getTexto() : null);	
				PdtImg img = getBlocoCirurgicoProcDiagTerapFacade().obterPdtImgPorDdtSeqESeq(dadoImg.getId().getSeqp(),
								dadoImg.getId().getDdtSeq());// C27	
				if (img != null){
					//linhaReport.setImagem(img.getImagem() != null ? new java.io.ByteArrayInputStream(img.getImagem()) : null);// f43
					linhaReport.setImagem(img.getImagem() != null ? img.getImagem() : null);// f43
				}
				listaC26C27.add(linhaReport);
				
//				if (img != null && img.getImagem() != null) {
//					try {
//						FileOutputStream output = new FileOutputStream(new File("/opt/target-file"+img.hashCode()));
//						IOUtils.write(img.getImagem(), output);
//					} catch (FileNotFoundException e) {
//						logWarn(e.getMessage(), e);
//					} catch (IOException e) {
//						logWarn(e.getMessage(), e);
//					}				
//				}
				
				/* byte[] buffer = null;   
				try {
					InputStream is = null;    
				      is = new FileInputStream(new File("/home/aghu/maca.jpg"));   
				      buffer = new byte[is.available()];   
				      is.read(buffer);   
				      is.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println(buffer);*/
				//linhaReport.setTeste(new java.io.ByteArrayInputStream(buffer));
				//linhaReport.setTeste(new java.io.ByteArrayInputStream(linhaReport.getImagem()));
				/*PdtImg img2 = getProcedimentoDiagnosticoTerapeuticoFacade().obterPdtImgPorDdtSeqESeq(dadoImg.getId().getSeqp(),
						dadoImg.getId().getDdtSeq());// C27
				System.out.println(img2.getCriadoEm());*/
			}
					
			return listaC26C27;
		}
		return null;
	}

	private List<LinhaReportVO> processarC29(Integer ddtSeq, Integer ddtSerMatricula) {
		DominioTipoAtuacao tipoAtuacao = DominioTipoAtuacao.CIRG;
		List<PdtProf> listaProf = getBlocoCirurgicoProcDiagTerapFacade().buscaPdtProfissaoPorDdtSeqETipoAtuacao(ddtSeq, tipoAtuacao, ddtSerMatricula);
		List<LinhaReportVO> listaC29 = new ArrayList<LinhaReportVO>();																		
		for (PdtProf prof : listaProf) {
			LinhaReportVO linhaReport = new LinhaReportVO();
			// f44
			String dpfNomeCrm;
			if (prof.getServidor() != null) {
				RapPessoasFisicas pessoa = obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(
						prof.getServidorPrf().getId().getMatricula(), prof.getServidorPrf().getId().getVinCodigo());
				if (pessoa != null) {
					dpfNomeCrm = "Dr. "
							+ pessoa.getNome()
							+ (getPesquisaInternacaoFacade()
									.buscarNroRegistroConselho(
											prof.getServidorPrf().getId().getVinCodigo(),
											prof.getServidorPrf().getId().getMatricula()) != null ? " CREMERS "
									+ getPesquisaInternacaoFacade()
											.buscarNroRegistroConselho(
													prof.getServidorPrf().getId().getVinCodigo(),
													prof.getServidorPrf().getId().getMatricula())
									: "");
				} else {
					dpfNomeCrm = "";
				}
			} else {
				dpfNomeCrm = "";
			}
			linhaReport.setTexto1(dpfNomeCrm);
			listaC29.add(linhaReport);
		}
		return listaC29;
	}

	private RapPessoasFisicas obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(Integer serMatricula, Short serVinCodigo) {
		// RESTRICAO VINDA DA FUNCAO RAPC_BUSCA_NOME
		RapServidores serv = getRegistroColaboradorFacade().obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(serMatricula, serVinCodigo);
		RapPessoasFisicas pessoa = serv.getPessoaFisica();
		return pessoa;
	}

	private String processarRN5(PdtDescricao pdtDescricao) {
		String notasAdicionais = null;
		if (DominioSituacaoDescricao.PEN.equals(pdtDescricao.getSituacao())) {
			notasAdicionais = "*** Esta Descrição está PENDENTE de conclusão, aguarde Descrição definitiva ***'";
		} else {
			//Conforme orientação de Tiago Felini fixo false
			Boolean suprimirLinhaPreliminar = Boolean.FALSE;
			if (suprimirLinhaPreliminar) {
				notasAdicionais = "           ";
			} else {
				if (DominioSituacaoDescricao.PRE.equals(pdtDescricao.getSituacao())) {
					notasAdicionais = "*** Esta Descrição é um laudo PRELIMINAR, aguarde Laudo definitivo ***";
				} else {
					notasAdicionais = "           ";
				}
			}
		}
		return notasAdicionais;
	}

	private String processarRN6(Short nadMaxSeqp) {
		String pNotasAdicionais = null;
		if ((nadMaxSeqp != null ? nadMaxSeqp : 0) > 0) {
			pNotasAdicionais = "*** DESCRIÇÃO DEFINITIVA COM NOTAS ADICIONAIS ***";
		} else {
			pNotasAdicionais = "     ";
		}
		return pNotasAdicionais;
	}

	private String processarRN8(String dmdPrompt) {
		Integer ind1 = 2;
		Integer ind2 = 2;
		String prompt = "A";
		if (dmdPrompt != null) {
			if ("Medicação pré-procedimento   :".equals(dmdPrompt) && ind1 != 1) {
				prompt = "Medicação pré-procedimento   :";
				ind1 = 1; // TODO rubens.silva verificar origem
			} else {
				if ("Medicação trans-procedimento :".equals(dmdPrompt) && ind2 != 1) {
					prompt = "Medicação trans-procedimento :";
					ind2 = 1; // TODO rubens.silva verificar origem
				} else {
					prompt = "        ";
				}
			}
		}
		return prompt;
	}

	// RN9 idêntica a RN11, basta alterar a passagem de parâmetros de C1 e C19 respectivamente
	private String processarRn9Rn11(Integer matricula, Short vinculo) throws ApplicationBusinessException {
		String vResponsavel2 = null;
		if (vinculo == null) {
			vinculo = 0;
		}
		BuscaConselhoProfissionalServidorVO conselhoProfissionalServidor = getPrescricaoMedicaFacade()
				.buscaConselhoProfissionalServidorVO(matricula, vinculo);
		if(conselhoProfissionalServidor != null && conselhoProfissionalServidor.getNome()!= null){
			vResponsavel2 = conselhoProfissionalServidor.getNome() + "   " + conselhoProfissionalServidor.getSiglaConselho() + "   "
			+ conselhoProfissionalServidor.getNumeroRegistroConselho();
		}
		return vResponsavel2;
	}
	
	private String processarRN12(MbcCirurgias cirurgia) {
		if(cirurgia.getAtendimento() ==  null){
			return "";
		}
		String leitoID = cirurgia.getAtendimento().getLeito() != null ? cirurgia.getAtendimento().getLeito().getLeitoID() : null;
		Short numeroQuarto = cirurgia.getAtendimento().getQuarto() != null ? cirurgia.getAtendimento().getQuarto().getNumero() : null;
		Short seqUnidadeFuncional = cirurgia.getAtendimento().getUnidadeFuncional().getSeq();
		
		String local = getProntuarioOnlineFacade().obterLocalizacaoPacienteParaRelatorio(leitoID, numeroQuarto, seqUnidadeFuncional);
		return local;		
	}

	private Boolean processarRN14(Short nadMaxSeqp) {
		if (nadMaxSeqp != null) {
			return true;
		}
		return false;
	}

	private Boolean processarRN15(Integer nroFilme) {		
		if (nroFilme == null) {
			return false;
		}
		return true;
	}

	private Boolean processarRN16(String nomeResponsavel) {		
		if (nomeResponsavel == null) {
			return false;
		}
		return true;
	}

	private Boolean processarRN17(String pjqNomeProj) {		
		if (pjqNomeProj == null) {
			return false;
		}
		return true;
	}

	private Boolean processarRN18(Integer matricula) {
		if (matricula == 0) {
			return false;
		} else {
			return true;
		}
	}

	private Boolean processarRN19(String sptNome) {
		if (sptNome == null) {
			return false;
		}
		return true;
	}

	private Boolean processarRN20(String pddObservacoesProc) {
		if (pddObservacoesProc == null) {
			return false;
		}
		return true;
	}

	private Boolean processarRN21(String sptObservacoes) {
		if (sptObservacoes == null) {
			return false;
		}
		return true;
	}

//	RN22 removido conforme alteração no documento de análise em 30/07/2012 por Tiago Felini 
//	private String processarRN22(Short nadMaxSeqp) {
//		Integer pNumero = 6;
//		if (nadMaxSeqp > 0) {
//			pNumero = pNumero + 1;
//		}
//		return pNumero.toString() + ".";
//	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IBlocoCirurgicoProcDiagTerapFacade getBlocoCirurgicoProcDiagTerapFacade() {
		return blocoCirurgicoProcDiagTerapFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	protected IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}

	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return this.pesquisaInternacaoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

}
