package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteDiagnosticos;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MamAltaDiagnosticos;
import br.gov.mec.aghu.model.MamAltaEvolucoes;
import br.gov.mec.aghu.model.MamAltaPrescricoes;
import br.gov.mec.aghu.model.MamAltaSumario;
import br.gov.mec.aghu.model.MamControles;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltasAmbulatoriasPolVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class AltasAmbulatoriaisPolON extends BaseBusiness {

	private static final String _NL = " \n";

	private static final Log LOG = LogFactory.getLog(AltasAmbulatoriaisPolON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	private static final long serialVersionUID = 3402911503925697716L;


	public List<AltasAmbulatoriasPolVO> pesquisarAltasAmbulatoriaisPol(Integer pacCodigo, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) throws ApplicationBusinessException {
		List<AltasAmbulatoriasPolVO> lista = ambulatorioFacade.pesquisarAltasAmbulatoriaisPol(pacCodigo, firstResult,maxResult, orderProperty, asc);
		
		for(AltasAmbulatoriasPolVO vo : lista){
			if(vo.getEspSeqPai() == null){//Simula and esp1.seq = decode(esp.esp_seq, null, esp.seq, esp.esp_seq) da VIEW V_AIP_POL_AMBUL
				vo.setEspSeqPai(vo.getEspSeq());
			}
			AghEspecialidades espT = getAghuFacade().obterAghEspecialidadesPorChavePrimaria(vo.getEspSeqPai());
			vo.setEspNomeEspecialidade(getAmbulatorioFacade().obterDescricaoCidCapitalizada(espT.getNomeEspecialidade(), CapitalizeEnum.TODAS));
			vo.setDataOrd(DateUtil.truncaData(vo.getDtHrInicio()));
			vo.setEspNomeAgenda(getAmbulatorioFacade().obterDescricaoCidCapitalizada(vo.getEspNomeAgenda(), CapitalizeEnum.TODAS));
			//vo.setTextoAltaCompleto(visualizaAlta(vo.getNumero()));
			visualizaAlta(vo);
			
			RapServidores serv;
			if(vo.getGrdSerMatricula() != null && vo.getGrdSerVinCodigo() != null){
				serv = getRegistroColaboradorFacade().obterRapServidoresPorChavePrimaria(new RapServidoresId(vo.getGrdSerMatricula(), vo.getGrdSerVinCodigo()));
			}else{
				serv = getRegistroColaboradorFacade().obterRapServidoresPorChavePrimaria(new RapServidoresId(vo.getEqpSerMatricula(), vo.getEqpSerVinCodigo()));
			}
			vo.setNomeProfissional((String) getPrescricaoMedicaFacade().buscaConsProf(serv.getId().getMatricula(), serv.getId().getVinCodigo())[1]);
		}
		
		
		return lista;
	}
	
	
	/**
	 * ORADB p_visualiza_alta
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	public String visualizaAlta(AltasAmbulatoriasPolVO vo) throws ApplicationBusinessException{
		List<MamAltaSumario> altasSumarios = getAmbulatorioFacade().pesquisarAltasSumariosParaAltasAmbulatoriais(vo.getNumero());
		
		StringBuffer vCabPend = new StringBuffer(100);
		StringBuffer vAlta    = new StringBuffer();
		//StringBuffer vAssinado    = new StringBuffer();
		
		for(MamAltaSumario altaSum: altasSumarios){
			if(DominioIndPendenteDiagnosticos.P.equals(altaSum.getPendente())){
				vCabPend.append("<<< Esta Alta não foi assinada e permanece na situação Pendente >>> \n");
				vAlta.append(getAltaAmb(altaSum.getSeq()));
				vAlta.append(vCabPend.toString() ).append( vAlta.toString());
				//vAssinado.append("N");
				/**
				 * v_cab_pend := CHR(10)||'<<< Esta Alta não foi assinada e permanece na situação Pendente >>>'|| CHR(10)||CHR(10);
       mamk_alta.mamp_get_alta_amb(r_als.seq, v_alta);
       v_alta := v_cab_pend || v_alta;
       k_variaveis.v_assinado := 'N';
				 */
				vo.setvAssinado("N");
			}else{
				vAlta.append(getAltaAmb(altaSum.getSeq()));
				/**
				 *  mamk_alta.mamp_get_alta_amb(r_als.seq, v_alta);
       k_variaveis.v_assinado := 'S';
				 */
				vo.setvAssinado("S");
			}
			
			vo.setvAlta(altaSum.getSeq());
		}
		
		vo.setTextoAltaCompleto(vAlta.toString());
		return vAlta.toString();
	}
	
	/**
	 * MAMP_GET_ALTA_AMB
	 * @param alsSeq
	 * @return
	 * alsSeq é a seq de MamAltaSumario
	 * @throws ApplicationBusinessException  
	 */
	public String getAltaAmb(Long alsSeq) throws ApplicationBusinessException{
		MamAltaSumario altaSumario = getAmbulatorioFacade().obterMamAltaSumarioPorId(alsSeq);
		
		StringBuffer vIdent = new StringBuffer(110);
		vIdent.append("1. IDENTIFICAÇÃO \n")
		.append("Nome : ")
		.append(getAmbulatorioFacade().obterDescricaoCidCapitalizada((altaSumario.getNome()) + "   ", CapitalizeEnum.TODAS))
		.append("   Prontuário : ")
		.append(altaSumario.getProntuario()).append(_NL)
		.append("Idade : ")
		.append(altaSumario.getIdadeDiasMesesAnos()).append( "  ")
		.append("Sexo : ")
		.append(altaSumario.getSexo().getDescricao()).append( _NL)
		.append("Agenda: ")
		.append(altaSumario.getDescEsp()).append( _NL)
		.append("Equipe Responsável: ")
		.append(getAmbulatorioFacade().obterDescricaoCidCapitalizada((altaSumario.getDescEquipe()) + _NL, CapitalizeEnum.TODAS))
		//.append(altaSumario.getPendente().getDescricao()).append( " \n")  
		.append(_NL);  
		
		List<MamAltaDiagnosticos> altasDiagnosticos = getAmbulatorioFacade().procurarAltaDiagnosticosBySumarioAltaEIndSelecionado(altaSumario, DominioSimNao.S);
		StringBuffer vDiag = new StringBuffer(32);
		vDiag.append("\n 2. DIAGNÓSTICOS  \n");
		for(MamAltaDiagnosticos diag: altasDiagnosticos){
			vDiag.append(diag.getDescricaoComplemento()).append( _NL);
		}
		
		
		List<MamAltaEvolucoes> altasEvolucoes = getAmbulatorioFacade().procurarAltaEvolucoesBySumarioAlta(altaSumario);
		StringBuffer vEvo = new StringBuffer(32);
		vEvo.append("\n 3. EVOLUÇÃO  \n");
		for(MamAltaEvolucoes evo: altasEvolucoes){
			vEvo.append(evo.getDescricao());
		}
		
		
		List<MamAltaPrescricoes> altasPrescricoes = getAmbulatorioFacade().procurarAltaPrescricoesBySumarioAltaEIndSelecionado(altaSumario,DominioSimNao.S);
		StringBuffer vPrescricao = new StringBuffer(40);
		vPrescricao.append(" 4. PRESCRIÇÃO PÓS-ALTA  \n");
		for(MamAltaPrescricoes presc: altasPrescricoes){
			vPrescricao.append(presc.getDescricao()).append( _NL);
		}
		
		StringBuffer vReferencia = new StringBuffer(32);
		vReferencia.append("\n 5. DESTINO  \n");
		if(altaSumario.getDestinoAlta() != null){
			vReferencia.append(altaSumario.getDestinoAlta().getDescricao()).append( _NL);
		}
		if(altaSumario.getEspecialidadeDestinoObservacaoDestino() != null){
			vReferencia.append(altaSumario.getEspecialidadeDestinoObservacaoDestino()).append( _NL);
		}

		StringBuffer vProfResp = new StringBuffer(getIdResAlta(alsSeq)).append( _NL);
		
		StringBuffer vExclusao = new StringBuffer();
		if(DominioIndPendenteDiagnosticos.E.equals(altaSumario.getPendente())){
			vExclusao = new StringBuffer("<<< ALTA AMBULATORIAL EXCLUÍDA >>>");
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(vIdent.toString())
		.append(vDiag)
		.append(vEvo)
		.append(vPrescricao)
		.append(vReferencia)
		.append(vProfResp)
		.append(vExclusao);
		
		return sb.toString();
	}

	protected IAmbulatorioFacade getAmbulatorioFacade(){
		return this.ambulatorioFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	/**
	 * ORADB mamc_get_id_res_alta
	 * @throws ApplicationBusinessException  
	 */
	public String getIdResAlta(Long alsSeq) throws ApplicationBusinessException{
		MamAltaSumario altaSumario = getAmbulatorioFacade().obterMamAltaSumarioPorId(alsSeq);
		// busca nome profissional que elaborou
		String vNome = null;
		if(altaSumario.getServidor() != null){
			Object[] buscaConsProf = getPrescricaoMedicaFacade().buscaConsProf(altaSumario.getServidor());
			vNome  = (String) buscaConsProf[1];
		}
		
		if(vNome != null){
			vNome = getAmbulatorioFacade().obterDescricaoCidCapitalizada(vNome, CapitalizeEnum.TODAS);
			vNome = new StringBuffer("\n Elaborado por " + vNome+ _NL).toString();
		}
		
		// busca nome profissional que validou
		String vNomeValida = null;
		if(altaSumario.getServidorValida() != null){
			Object[] buscaConsProf = getPrescricaoMedicaFacade().buscaConsProf(altaSumario.getServidorValida());
			vNomeValida  = (String) buscaConsProf[1];
		}
		
		if(vNomeValida != null){
			vNomeValida = new StringBuffer("Assinado por "
					+ getAmbulatorioFacade().obterDescricaoCidCapitalizada(vNomeValida, CapitalizeEnum.TODAS)
					+ "  em: "
					+ DateUtil.obterDataFormatadaHoraMinutoSegundo(altaSumario
							.getDthrValida())+ _NL).toString();
		}else{
			vNomeValida = "";
		}
		
		StringBuffer vSuperVisor = new StringBuffer(32);
		MamControles mamControle = getAmbulatorioFacade().obterMamControlePorNumeroConsulta(altaSumario.getConsulta().getNumero());
		if(mamControle.getSupervisor() != null){
			vSuperVisor.append("Supervisionado por " + mamControle.getSupervisor()+ _NL);
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(vNome)
		.append(vNomeValida)
		.append(vSuperVisor.toString());
		
		return sb.toString();
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	public Long pesquisarAltasAmbulatoriaisPolCount(Integer pacCodigo) {
		return getAmbulatorioFacade().pesquisarAltasAmbulatoriaisPolCount(pacCodigo);
	}
	
}
