package br.gov.mec.aghu.transplante.business;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.dominio.DominioTipoAlogenico;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.dominio.DominioTipoTransplanteCombo;
import br.gov.mec.aghu.model.MtxCriterioPriorizacaoTmo;
import br.gov.mec.aghu.transplante.dao.MtxCriterioPriorizacaoTmoDAO;
import br.gov.mec.aghu.transplante.dao.MtxExtratoTransplantesDAO;
import br.gov.mec.aghu.transplante.dao.MtxTransplantesDAO;
import br.gov.mec.aghu.transplante.vo.FasesProntuarioVO;
import br.gov.mec.aghu.transplante.vo.FiltroTempoPermanenciaListVO;
import br.gov.mec.aghu.transplante.vo.FiltroTempoSobrevidaTransplanteVO;
import br.gov.mec.aghu.transplante.vo.PacienteTransplanteMedulaOsseaVO;
import br.gov.mec.aghu.transplante.vo.PacienteTransplanteOrgaoVO;
import br.gov.mec.aghu.transplante.vo.RelatorioPermanenciaFasesVO;
import br.gov.mec.aghu.transplante.vo.RelatorioPermanenciaPacienteListaTransplanteVO;
import br.gov.mec.aghu.transplante.vo.RelatorioSobrevidaPacienteTransplanteVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class RelatorioTempoPacienteListaTransplanteRN extends BaseBusiness {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7832566271916651133L;
	private static final Log LOG = LogFactory.getLog(RelatorioTempoPacienteListaTransplanteRN.class);


	@Inject
	private MtxTransplantesDAO mtxTransplantesDAO;
	
	@Inject
	private MtxCriterioPriorizacaoTmoDAO mtxCriterioPriorizacaoTmoDAO;
	
	@Inject
	private MtxExtratoTransplantesDAO mtxExtratoTransplantesDAO;
	
	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;
	
	
	
	 public List<RelatorioPermanenciaPacienteListaTransplanteVO> montarRelatorioTempoPermanenciaPacienteLista(FiltroTempoPermanenciaListVO filtro){
			return concatenarTipoTransplante(filtro);
		 }
		 
		 /***
		  * RN01	Regra para o campo “Tipo” ( ID 18 )
			Se o “Tipo Transplante” ( ID 4 ) for igual a TMO:
			•	Deverá concatenar os campos C5.TIPO e o campo C5.TIPO_ALOGENICO 
			Se não
			•	Deverá mostrar o campo C4.ORGAO
		  */
		 private List<RelatorioPermanenciaPacienteListaTransplanteVO> concatenarTipoTransplante(FiltroTempoPermanenciaListVO filtro){
			 List<RelatorioPermanenciaPacienteListaTransplanteVO> retorno = null;
			 if(filtro.getTipoTransplante().equals(DominioTipoTransplanteCombo.T)){
				 List<PacienteTransplanteMedulaOsseaVO> consultarPacientesTransplanteMedulaOssea = mtxTransplantesDAO.consultarPacientesTransplanteMedulaOssea(filtro.getProntuarioNome(), filtro.getDataInicio(), filtro.getDataFim(), filtro.getTipoTMO(), filtro.getTipoAlogenico(), filtro.getOrdenacao());
				 for (PacienteTransplanteMedulaOsseaVO current : consultarPacientesTransplanteMedulaOssea) {
					//RN1
					 if(retorno == null){
						 retorno = new ArrayList<RelatorioPermanenciaPacienteListaTransplanteVO>();
					 }
					 RelatorioPermanenciaPacienteListaTransplanteVO aux = new RelatorioPermanenciaPacienteListaTransplanteVO();
					 if(filtro.getTipoTransplante().equals(DominioTipoTransplanteCombo.T)) {
						 if(current.getTipoTmo() != null && current.getTipoTmo().equals(DominioSituacaoTmo.G)) {
							 StringBuilder tipo = new StringBuilder();
							 tipo.append(current.getTipoTmo().getDescricao()).append(current.getTipoAlogenico() != null ? " - " +current.getTipoAlogenico().getDescricao():"");
							 aux.setTipo(tipo.toString());
						 }else if(current.getTipoTmo() != null && current.getTipoTmo().equals(DominioSituacaoTmo.U)) {
							 aux.setTipo(current.getTipoTmo().getDescricao());
								
						 }
					 }
					 //RN02
					 montarCampoPermanencia(current,aux);
					 //RN03
					 montarCampoEscore(current,aux);
					 //RN04
					 montarCampoFases(current.getTransplanteSeq(),aux);
					 aux.setProntuario( (current.getProntuario() != null ? String.valueOf(current.getProntuario()):"") );
					 aux.setNome(current.getNome());
					 retorno.add(aux);
				}
			 }else{
				 List<PacienteTransplanteOrgaoVO> consultarPacientesTransplanteOrgaos = mtxTransplantesDAO.consultarPacientesTransplanteOrgaos(filtro.getProntuarioNome(), filtro.getDataInicio(), filtro.getDataFim(), filtro.getTipoOrgao(), filtro.getOrdenacao());
				 for (PacienteTransplanteOrgaoVO current : consultarPacientesTransplanteOrgaos) {
					//RN1
					 if(retorno == null){
						 retorno = new ArrayList<RelatorioPermanenciaPacienteListaTransplanteVO>();
					 }
					 RelatorioPermanenciaPacienteListaTransplanteVO aux = new RelatorioPermanenciaPacienteListaTransplanteVO();
					 aux.setTipo(current.getOrgao().getDescricao());
					 //RN02
					 montarCampoPermanencia(current,aux);
					 //RN03
					 montarCampoEscore(current,aux);
					 //RN04
					 montarCampoFases(current.getTransplanteSeq(),aux);
					 aux.setProntuario( (current.getProntuario() != null ? String.valueOf(current.getProntuario()):"") );
					 aux.setNome(current.getNomePaciente());
					 retorno.add(aux);

				 }
				 
			 }
			 return retorno;
		 }
		 
		 /**
		  * RN02	Regra para o campo “Permanência” ( ID 19 )
			Se o “Tipo Transplante” ( ID 4 ) for igual a TMO:
			•	Calcular ( Data atual menos C5.INGRESSO) e exibir tempo em dias
			Se não
			•	Calcular ( Data atual menos C4.INGRESSO) e exibir tempo em dias
		  */
		 private void montarCampoPermanencia(Object objeto,  RelatorioPermanenciaPacienteListaTransplanteVO aux){
			 if(objeto instanceof PacienteTransplanteMedulaOsseaVO){
					 PacienteTransplanteMedulaOsseaVO paciente = (PacienteTransplanteMedulaOsseaVO)objeto;
					 //•	Calcular ( Data atual menos C5.INGRESSO) e exibir tempo em dias
					 int dias  = DateUtil.obterQtdDiasEntreDuasDatasTruncadas(paciente.getDataIngresso(),new Date());
					 aux.setPermanencia(String.valueOf(dias));
			 }else if(objeto instanceof PacienteTransplanteOrgaoVO){
					 PacienteTransplanteOrgaoVO paciente = (PacienteTransplanteOrgaoVO)objeto;
					 //•	Calcular ( Data atual menos C4.INGRESSO) e exibir tempo em dias
					 int dias  = DateUtil.obterQtdDiasEntreDuasDatasTruncadas(paciente.getDataIngresso(),new Date());
					 aux.setPermanencia(String.valueOf(dias));
				 }
		 }
		 
		 /**
		  * RN03	Regra para o campo “Escore” ( ID 20 )
			Se o “Tipo Transplante” ( ID 4 ) for igual a TMO então deverá apresentar esse campo com a formula (X * 0,33) + Y, onde:
			•	X: “Data atual” menos C5.INGRESSO em dias.
			•	Y: Coeficiente obtido na consulta C3.
			•	Se idade atual do paciente for menor de 13 anos, somar 20 ao escore total.
		  */
		 private void montarCampoEscore(Object objeto, RelatorioPermanenciaPacienteListaTransplanteVO aux){
			 if(objeto instanceof PacienteTransplanteMedulaOsseaVO){
				 PacienteTransplanteMedulaOsseaVO paciente = (PacienteTransplanteMedulaOsseaVO)objeto;
				 Double somaEscore = new Double(0);
				 int x = DateUtil.obterQtdDiasEntreDuasDatasTruncadas(paciente.getDataIngresso(),new Date());
				 somaEscore = x*0.33;
				 MtxCriterioPriorizacaoTmo ctmo = mtxCriterioPriorizacaoTmoDAO.obterCoeficiente(paciente.getCptSeq());
				 int y = 0;
				 if(ctmo != null){
					 y = ctmo.getCriticidade() + ctmo.getGravidade();
					 somaEscore+=y;
				 }
				 int idade = CoreUtil.calculaIdade(paciente.getDataNascimento());
				 if(idade < 13){
					 somaEscore+=20;
				 }
				 DecimalFormat formato = new DecimalFormat("#0.000");
				 try {
					 aux.setEscore(formato.parse(somaEscore.toString()).toString());
				 } catch (ParseException e) {
					LOG.error(e.getMessage());
					aux.setEscore(somaEscore.toString());
				 }
			 }
		}
		 
		 /**
		  * RN04	Regra para o campo “Fases” ( ID 21 )
			Executar a consulta C6, para buscar as Fases existentes para cada Prontuário.
			Importante: 
			•	Caso a consulta não retorne registros, esse campo ficará vazio, porém o restante das informações do prontuário, deverão ser impressas;
			•	Concatenar os campos C6.SITUACAO, C6.DATA_SITUACAO e C6.PERMANENCIA para compor a descrição da Fase;
			•	O campo C6.DATA_SITUACAO deverá ser gravado no formato DD/MM/YYYY;
			•	O campo C6.PERMANENCIA deverá ser gravado no formato “9.999”.
		  */
		 private void montarCampoFases(Integer trpSeq, RelatorioPermanenciaPacienteListaTransplanteVO aux){
			 List<FasesProntuarioVO> fases = this.mtxExtratoTransplantesDAO.consultarFasesProntuario(trpSeq);
			 for (int i = 0; i < fases.size(); i++) {
				 if(aux.getFases() == null){
					 aux.setFases(new ArrayList<RelatorioPermanenciaFasesVO>());
				 }
				 FasesProntuarioVO current = fases.get(i);
				 Date dataProximoSeExiste = new Date();
				 if((i < (fases.size()-1)) && (fases.get(i+1) != null)){
					dataProximoSeExiste = fases.get(i+1).getDataOcorrencia();
				 }
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				
				RelatorioPermanenciaFasesVO fase = new RelatorioPermanenciaFasesVO();
				fase.setTextoStatus(current.getSituacao().retornarDescricaoCompleta()+ " <SUBSTITUIR> ");
				fase.setDataFormatada(formatter.format(current.getDataOcorrencia()));
				fase.setTextoPermanencia("com permanência de "+DateUtil.obterQtdDiasEntreDuasDatasTruncadas(current.getDataOcorrencia(),dataProximoSeExiste) +" dias");
				
				if(current.getSituacao().equals(DominioSituacaoTransplante.A)){
					fase.setTextoStatus(fase.getTextoStatus().replace("<SUBSTITUIR>", "cadastrado em: "));
				}else if(current.getSituacao().equals(DominioSituacaoTransplante.E)){
					fase.setTextoStatus(fase.getTextoStatus().replace("<SUBSTITUIR>", "ingresso em: "));
				}else{
					fase.setTextoStatus(fase.getTextoStatus().replace("<SUBSTITUIR>", "em: "));
				}
				aux.getFases().add(fase);				
			}
		 }
		 
		@Override
		protected Log getLogger() {
			return LOG;
		}
	
		public List<RelatorioSobrevidaPacienteTransplanteVO> montarRelatorioSobrevidaPacienteTransplante(
				FiltroTempoSobrevidaTransplanteVO filtro) {
			return concatenarTipoTransplante(filtro);
		}
	
		/**
		 * #41792-RN1
		 */
		public List<RelatorioSobrevidaPacienteTransplanteVO> concatenarTipoTransplante(
				FiltroTempoSobrevidaTransplanteVO filtro) {
			List<RelatorioSobrevidaPacienteTransplanteVO> retorno = null;
			if (filtro.getTipoTransplante().equals(DominioTipoTransplanteCombo.T)) {
				List<PacienteTransplanteMedulaOsseaVO> consultarPacientesTransplanteMedulaOssea = this.mtxTransplantesDAO
						.consultarPacientesTransplanteMedulaOsseaSobrevida(
								filtro.getProntuarioNome(), filtro.getDataInicio(),
								filtro.getDataFim(), filtro.getTipoTMO(),
								filtro.getTipoAlogenico(), filtro.getOrdenacao());
				for (PacienteTransplanteMedulaOsseaVO current : consultarPacientesTransplanteMedulaOssea) {
					if (retorno == null) {
						retorno = new ArrayList<RelatorioSobrevidaPacienteTransplanteVO>();
					}
					RelatorioSobrevidaPacienteTransplanteVO aux = new RelatorioSobrevidaPacienteTransplanteVO();
					aux.setProntuario((current.getProntuario() != null ? gerarMascaraProntuario(String.valueOf(current.getProntuario())) : " " ));
									
					// RN1
					if (current.getTipoIndTmo() != null
							&& !"".equals(current.getTipoIndTmo())) {
						aux.setTipo(DominioSituacaoTmo.valueOf(current.getTipoIndTmo())
								.getDescricao());
					}
					
					if(filtro.getTipoTransplante().equals(DominioTipoTransplanteCombo.T)) {
						if(current.getTipoIndTmo() != null && current.getTipoIndTmo().equalsIgnoreCase("G")) {
							 StringBuilder tipo = new StringBuilder();
							 tipo.append(DominioSituacaoTmo.valueOf(current.getTipoIndTmo()).getDescricao()).append(DominioTipoAlogenico.valueOf(current.getTipoIndAlogenico()) != null ? " - " +DominioTipoAlogenico.valueOf(current.getTipoIndAlogenico()).getDescricao():"");
							 aux.setTipo(tipo.toString());
						}else if(current.getTipoIndTmo() != null && current.getTipoIndTmo().equals(DominioSituacaoTmo.U)) {
							aux.setTipo(current.getTipoIndTmo());
						}
					}
					aux.setNome(current.getNome());
					SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
					aux.setDataTransplante(f.format(current.getDataTransplante()));
					
					Timestamp dataUltimoAtd = this.aghAtendimentoDAO.consultarDataUltimoAtendimentoUnion(current.getProntuario());
					if( dataUltimoAtd != null  && dataUltimoAtd.after(current.getDataTransplante()) ){	
						//TODO PErguntar sobrevida se data Ultimo Atendimento vir vazio
						aux.setSobrevida(String.valueOf(DateUtil.obterQtdDiasEntreDuasDatasTruncadas(current.getDataTransplante(), dataUltimoAtd)));
						aux.setDataUltimoAtendimento(f.format(dataUltimoAtd.getTime()));
						retorno.add(aux);
					}
					
	
				}
	
			} else if (filtro.getTipoTransplante().equals(
					DominioTipoTransplanteCombo.O)) {
				List<PacienteTransplanteOrgaoVO> consultarPacientesTransplanteOrgaos = this.mtxTransplantesDAO
						.consultarPacientesTransplanteOrgaosSobrevida(
								filtro.getProntuarioNome(), filtro.getDataInicio(),
								filtro.getDataFim(), filtro.getTipoOrgao(),
								filtro.getOrdenacao());
				for (PacienteTransplanteOrgaoVO current : consultarPacientesTransplanteOrgaos) {
	
					if (retorno == null) {
						retorno = new ArrayList<RelatorioSobrevidaPacienteTransplanteVO>();
					}
					RelatorioSobrevidaPacienteTransplanteVO aux = new RelatorioSobrevidaPacienteTransplanteVO();
					aux.setProntuario((current.getProntuario() != null ? gerarMascaraProntuario(String.valueOf(current.getProntuario())) : " " ));
								
					// RN1
					aux.setTipo(DominioTipoOrgao.valueOf(current.getTipoOrgao())
							.getDescricao());
					aux.setNome(current.getNomePaciente());
					SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
					aux.setDataTransplante(f.format(current.getDataTransplante()));
					
					Timestamp dataUltimoAtd = this.aghAtendimentoDAO
							.consultarDataUltimoAtendimentoUnion(current.getProntuario());
					if( dataUltimoAtd != null  && dataUltimoAtd.after(current.getDataTransplante()) ){
						//TODO PErguntar sobrevida se data Ultimo Atendimento vir vazio
						aux.setSobrevida(String.valueOf(DateUtil.obterQtdDiasEntreDuasDatasTruncadas(current.getDataTransplante(), dataUltimoAtd)));
						aux.setDataUltimoAtendimento(f.format(dataUltimoAtd.getTime()));
						retorno.add(aux);
					}
					
				}
			}
			return retorno;
		}
	
		private String gerarMascaraProntuario(String pront){
			StringBuilder str = new StringBuilder();
			str.append(pront.substring(0,pront.length() - 1))
			.append("/" + pront.substring(pront.length()-1, pront.length()));
			return str.toString();
		}
	

			
			
}
