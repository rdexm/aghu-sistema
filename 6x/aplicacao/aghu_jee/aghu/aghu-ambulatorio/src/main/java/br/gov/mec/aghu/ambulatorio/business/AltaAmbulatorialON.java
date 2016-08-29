package br.gov.mec.aghu.ambulatorio.business;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacTipoProcedSisregDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAltaDiagnosticosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAltaEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAltaPrescricoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAltasSumarioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoAltaDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteDiagnosticos;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamAltaDiagnosticos;
import br.gov.mec.aghu.model.MamAltaEvolucoes;
import br.gov.mec.aghu.model.MamAltaPrescricoes;
import br.gov.mec.aghu.model.MamAltaSumario;
import br.gov.mec.aghu.model.MamTipoAlta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class AltaAmbulatorialON extends BaseBusiness implements Serializable{

	private static final long serialVersionUID = 7811642750081624608L;
	
	private static final Log LOG = LogFactory.getLog(AltaAmbulatorialON.class);

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private MamTipoAltaDAO mamTipoAltaDAO;
	
	@Inject
	private MamAltaPrescricoesDAO mamAltaPrescricoesDAO;
	
	@Inject
	private	MamAltaDiagnosticosDAO mamAltaDiagnosticosDAO;
	
	@Inject
	private AacTipoProcedSisregDAO aacTipoProcedSisregDAO;
	
	@Inject
	private MamAltasSumarioDAO mamAltasSumarioDAO;
	
	@Inject
	private MamAltaEvolucoesDAO mamAltaEvolucoesDAO;
	
	@Inject
	private IAghuFacade iAghuFacade;
	
	@Inject
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;
	
	private enum AltaAmbulatorialONExceptionCode implements BusinessExceptionCode{
		MSG_ALTA_AMBULATORIAL_CID_DUPLICADO_DIAGNOSTICO
	}

	public List<AghAtendimentos> pesquisarAtendimentoParaAltaAmbulatorial(Integer codigoPac, Integer atdSeq){
		List<AghAtendimentos> atendimentos = getPrescricaoAmbulatorialON().pesquisarAtendimentoParaPrescricaoMedica(
				codigoPac, atdSeq, null, Arrays.asList(DominioOrigemAtendimento.A));
		
		return atendimentos;
	}
	
	public MamAltaSumario persistirMamAltaSumario(AipPacientes paciente, AghAtendimentos atendimento, String usuarioLogado) throws ApplicationBusinessException{
		RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(usuarioLogado, new Date());
		MamTipoAlta tipoAlta = getMamTipoAltaDAO().obterPorChavePrimaria(2);//Verificar regras
		
		Integer idadeAnos  = DateUtil.obterQtdAnosEntreDuasDatas(paciente.getDtNascimento(), new Date());
		Integer idadeMeses = (DateUtil.obterQtdMesesEntreDuasDatas(paciente.getDtNascimento(), new Date()) % 12);
		Integer idadeDias  = (((DateUtil.obterQtdMesesEntreDuasDatas(paciente.getDtNascimento(), new Date()) % 1)* 1000000) * ((Calendar.getInstance()).getActualMaximum(Calendar.DAY_OF_MONTH)/ 1000000));
		
		MamAltaSumario altaSumario = new MamAltaSumario();
		altaSumario.setConsulta(atendimento.getConsulta());
		altaSumario.setPaciente(paciente);
		altaSumario.setProntuario(paciente.getProntuario());
		altaSumario.setNome(paciente.getNome());
		altaSumario.setIdadeDias(idadeDias.byteValue());
		altaSumario.setIdadeMeses(idadeMeses.byteValue());
		altaSumario.setIdadeAnos(idadeAnos.shortValue());
		altaSumario.setSexo(paciente.getSexo());
		altaSumario.setEquipe(atendimento.getConsulta().getGradeAgendamenConsulta().getEquipe());
		altaSumario.setDescEquipe(atendimento.getConsulta().getGradeAgendamenConsulta().getEquipe().getNome());
		altaSumario.setEspecialidade(atendimento.getEspecialidade());
		altaSumario.setDescEsp(atendimento.getEspecialidade().getNomeEspecialidade());
		altaSumario.setPendente(DominioIndPendenteDiagnosticos.P);
		altaSumario.setImpresso(false);
		altaSumario.setDthrCriacao(new Date());
		altaSumario.setServidor(servidorLogado);
		altaSumario.setTipoAlta(tipoAlta);
//		altaSumario.setCategoriaProfissional(categoriaProfissional); //Preencher
//		altaSumario.setDestinoAlta(destinoAlta);
//		altaSumario.setRetornoAgenda(retornoAgenda);
		if(atendimento.getEspecialidade() != null){
			altaSumario.setEspecialidadePai(atendimento.getEspecialidade().getEspecialidade());
			if(atendimento.getEspecialidade().getEspecialidade() != null){
				altaSumario.setDescEsp(atendimento.getEspecialidade().getEspecialidade().getNomeEspecialidade());
			}
		}
		altaSumario.setDtNascimento(paciente.getDtNascimento());
		
		getMamAltasSumarioDAO().persistir(altaSumario);
		return altaSumario;
	}
	
	public void persistirMamAltaEvolucoes(MamAltaEvolucoes evolucao){
		this.mamAltaEvolucoesDAO.persistir(evolucao);
	}
	
	public void desbloquearMamAltaSumario(MamAltaSumario altaSumario){
		altaSumario.setPendente(DominioIndPendenteDiagnosticos.A);
		getMamAltasSumarioDAO().persistir(altaSumario);
	}
	
	public void persistirMamAltaDiagnosticos(MamAltaDiagnosticos diagnostico) throws ApplicationBusinessException{
		if(diagnostico.getIndSelecionado() == null){
			diagnostico.setIndSelecionado(false);
		}
		diagnostico.setDescricao(diagnostico.getCid().getDescricao());
		if(diagnostico.getSeq() == null){
			List<MamAltaDiagnosticos> diagnosticos = getMamAltaDiagnosticosDAO()
					.pesquisarAltaDiagnosticosPorSumarioAltaECid(
							diagnostico.getAltaSumario().getSeq(),
							diagnostico.getCid().getSeq());
			
			if(diagnosticos != null && !diagnosticos.isEmpty()){
				throw new ApplicationBusinessException(AltaAmbulatorialONExceptionCode.MSG_ALTA_AMBULATORIAL_CID_DUPLICADO_DIAGNOSTICO);
			}
		}
		getMamAltaDiagnosticosDAO().persistir(diagnostico);
	}
	
	public void persistirMamAltaPrescricoes(MamAltaPrescricoes prescricao){
		if(prescricao.getIndSelecionado() == null){
			prescricao.setIndSelecionado(false);
		}
		getMamAltaPrescricoesDAO().persistir(prescricao);
	}
	
	public void removerMamAltaDiagnosticos(MamAltaDiagnosticos diagnostico){
		getMamAltaDiagnosticosDAO().remover(diagnostico);
	}
	
	public void removerMamAltaPrescricoes(MamAltaPrescricoes prescricao){
		getMamAltaPrescricoesDAO().remover(prescricao);
	}
	
	public void atualizarIndImpressaoAltaAmb(Long seq) {
		MamAltaSumario mamAltaSumario = mamAltasSumarioDAO.obterPorChavePrimaria(seq);
		mamAltaSumario.setImpresso(Boolean.TRUE);
		mamAltasSumarioDAO.atualizar(mamAltaSumario);
	}
	
	protected MamAltaPrescricoesDAO getMamAltaPrescricoesDAO() {
		return mamAltaPrescricoesDAO;
	} 
	
	protected MamAltaDiagnosticosDAO getMamAltaDiagnosticosDAO() {
		return mamAltaDiagnosticosDAO;
	}
	
	protected MamTipoAltaDAO getMamTipoAltaDAO() {
		return this.mamTipoAltaDAO;
	}
	
	protected AacTipoProcedSisregDAO getAacTipoProcedSisregDAO() {
		return aacTipoProcedSisregDAO;
	}
	
	protected MamAltasSumarioDAO getMamAltasSumarioDAO() {
		return mamAltasSumarioDAO;
	}
	
	protected IAghuFacade getAghuFacade(){
		return iAghuFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade(){
		return iRegistroColaboradorFacade;
	}
	
	protected PrescricaoAmbulatorialON getPrescricaoAmbulatorialON(){
		return new PrescricaoAmbulatorialON();
	}
}