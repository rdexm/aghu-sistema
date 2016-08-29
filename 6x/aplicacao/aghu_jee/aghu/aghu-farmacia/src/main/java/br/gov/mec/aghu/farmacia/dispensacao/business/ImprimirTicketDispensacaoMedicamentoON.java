package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.vo.TicketMdtoDispensadoVO;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * Classe de geracao e impressao do ticket de dispensacao
 * de medicamentos em impressora MATRICIAL.
 * 
 */
@Stateless
public class ImprimirTicketDispensacaoMedicamentoON extends BaseBusiness implements Serializable {
	
	//Injecoes de dependencias necessarias neste caso de uso
	
	private static final long serialVersionUID = 7824277979301926570L;
	
	private static final Log LOG = LogFactory.getLog(ImprimirTicketDispensacaoMedicamentoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO ;

	
	public String gerarTicketDispensacaoMdto(Integer prontuario, 
			String local, String paciente, 
			String prescricao_inicio, 
			String prescricao_fim, 
			Boolean dispencacaoComMdto, Boolean prescricaoEletronica,
			List<TicketMdtoDispensadoVO> listaMdto,
			String relatorioEmitidoPor) 
		throws ApplicationBusinessException{

		//Criacao do StringBuffer que recebera as informacoes do ticket e montara o arquivo TXT a ser enviado para a impressora
		StringBuffer matricial = new StringBuffer();
		/*StringBuffer matricial = ticketMdtoDispensadosSemMedicamento(prontuario, local, paciente,
				prescricao_inicio, prescricao_fim);*/
		
		if(prescricaoEletronica){
			matricial.append(completaComCaracter(11, '-'))
			.append(" PRESCRICAO ELETRONICA ")
			.append(completaComCaracter(11, '-'));
		}else{
			matricial.append(completaComCaracter(9, '-'))
			.append(" PRESCRICAO NAO ELETRONICA ")
			.append(completaComCaracter(9, '-'));
		}
		matricial.append('\n')
		.append(getCabecalhoComumTicketMdtoDispensados(prontuario, local, paciente, prescricao_inicio, prescricao_fim));
		
		if(dispencacaoComMdto){
			CoreUtil.ordenarLista(listaMdto, TicketMdtoDispensadoVO.Fields.MDTO_CONTROLADO.toString(), Boolean.FALSE);
			matricial.append(getMdtosDispensadosTicket(listaMdto, Boolean.TRUE))
			.append(getMdtosDispensadosTicket(listaMdto, Boolean.FALSE));
		}
		
		matricial.append(
				getRodapeComumTicketMdtoDispensados(relatorioEmitidoPor, (listaMdto != null && !listaMdto.isEmpty()))
				);

		return matricial.toString();
	}
	
	private Object getMdtosDispensadosTicket(
			List<TicketMdtoDispensadoVO> listaMdto, Boolean indMdtoControlado) {
		
		Boolean possuiTipoMdto = Boolean.FALSE;
		//Verifica se a lista de mdto tem algum medicamento com o mesmo tipo de controle
		for(TicketMdtoDispensadoVO vo : listaMdto){
			if(vo.getMdtoControlado().equals(indMdtoControlado)){
				possuiTipoMdto = Boolean.TRUE;
				break;
			}
		}
		
		if(!possuiTipoMdto){
			return "";
		}
		
		
		StringBuffer matricial = new StringBuffer(70);
		
		matricial.append(completaComCaracter(7, '-'))
		.append(completaComCaracter(30, '='))
		.append(completaComCaracter(8, '-'))
		.append('\n');
		
		if(indMdtoControlado){
			matricial.append(completaComCaracter(12, ' '))
			.append("MEDICAMENTOS CONTROLADOS ");
		}else{
			matricial.append(completaComCaracter(8, ' '))
			.append("MEDICAMENTOS NAO CONTROLADOS ");
		}
		matricial.append('\n')
		
		.append(completaComCaracter(7,  '-'))
		.append(completaComCaracter(30, '='))
		.append(completaComCaracter(8,  '-'))
		.append('\n')
		
		/*Linha em branco*/ 
		.append(completaComCaracter(45, ' ')).append('\n')
		
		.append("MEDICAMENTO ")
		/*Linha em branco*/ 
		.append(completaComCaracter(23, ' '))
		.append("QUANTIDADE")
		.append('\n');
		
		Integer qtdMaxCarac = 45;
		Integer qtdeMaxQtd = 7;
		Integer qtdCaracQtdMdto = qtdeMaxQtd+5;//1 em branco + 7 qtd (99,9999) + 1 em branco + 3 apresentacao
		Integer qtdCaracDescMdto = qtdMaxCarac - qtdCaracQtdMdto;

		matricial.append(completaComCaracter(qtdMaxCarac-qtdCaracQtdMdto, '-'))
		.append(completaComCaracter(2, ' '))
		.append(completaComCaracter(qtdCaracQtdMdto-2, '-'))
		.append('\n');
		
		for(TicketMdtoDispensadoVO vo : listaMdto){
			if(vo.getMdtoControlado().equals(indMdtoControlado)){
				//break;
				String descricao = "";
				String descricaoRestante = "";
				if(vo.getMdtoDescricao().length() > qtdCaracDescMdto){
					String[] descricaoArray = vo.getMdtoDescricao().split(" ");
					for (String quebra : descricaoArray) {
						if((descricao.length() + quebra.length() + 1) < qtdCaracDescMdto && "".equals(descricaoRestante)){
							if("".equals(descricao)){
								descricao = descricao.concat(quebra);
							}else{
								descricao = descricao.concat(" ").concat(quebra);
							}
						}else{
							if("".equals(descricaoRestante)){
								descricaoRestante = descricaoRestante.concat(quebra);
							}else{
								descricaoRestante = descricaoRestante.concat(" ").concat(quebra);
							}
						}
					}
					matricial.append(descricao)
					.append(completaComCaracter(qtdCaracDescMdto - descricao.length(),  ' '));
				}else{
					matricial.append(vo.getMdtoDescricao())
					.append(completaComCaracter(qtdCaracDescMdto - vo.getMdtoDescricao().length(),  ' '));
				}
				
				addMdtoQtdEApresentacao(matricial, qtdeMaxQtd, vo);
				
				if(vo.getMdtoDescricao().length() > qtdCaracDescMdto){
					if(descricaoRestante.length() > qtdMaxCarac){
						descricaoRestante = descricaoRestante.substring(0, qtdMaxCarac);
					}
					matricial.append(descricaoRestante).append('\n');
				}
			}
		}
		if(indMdtoControlado){
			matricial.append('\n')
			.append('\n');
		}
		
		return matricial;
	}

	private void addMdtoQtdEApresentacao(StringBuffer matricial,
			Integer qtdeMaxQtd, TicketMdtoDispensadoVO vo) {
		matricial.append(' ')//1º em branco
		
		.append(completaComCaracter(qtdeMaxQtd - vo.getQuantidade().length(), ' '))
		.append(vo.getQuantidade())
		
		.append(' ')//2º em branco
		.append(vo.getMdtoSigla() != null ? vo.getMdtoSigla() : "")
		.append('\n');
	}

	private StringBuffer getRodapeComumTicketMdtoDispensados(String relatorioEmitidoPor, Boolean relPossuiMdto) {
		StringBuffer matricial = new StringBuffer(60);
		
		/*Linha em branco*/ matricial.append(completaComCaracter(45, ' ')).append('\n');
		
		if(relPossuiMdto){
			matricial.append(completaComCaracter(15, '-'))
			.append(" CONFERIDO POR ")
			.append(completaComCaracter(15, '-'))
			
			/*Linha em branco*/ 
			.append(completaComCaracter(45, ' ')).append('\n')
			/*Linha em branco*/ 
			.append(completaComCaracter(45, ' ')).append('\n')
			/*Linha em branco*/ 
			.append(completaComCaracter(45, ' ')).append('\n')
			
			.append(completaComCaracter(15, '-'))
			.append(" RECEBIDO  POR ")
			.append(completaComCaracter(15, '-'))
			/*Linha em branco*/ 
			.append(completaComCaracter(45, ' ')).append('\n')
			/*Linha em branco*/ 
			.append(completaComCaracter(45, ' ')).append('\n')
			/*Linha em branco*/ 
			.append(completaComCaracter(45, ' ')).append('\n');
		}
		
		matricial.append(completaComCaracter(45, '-')).append('\n')
		.append("EMISSAO: ")
		.append(DateUtil.dataToString(new Date(), "dd/MM/yyyy"))
		.append(" as ")
		.append(DateUtil.dataToString(new Date(), "HH:mm"))
		.append('\n')
		
		
		.append(completaComCaracter(9, ' '))
		.append(relatorioEmitidoPor)
		.append('\n')
		
		.append(completaComCaracter(7, '\n'));
		
		return matricial;
	}

	private StringBuffer getCabecalhoComumTicketMdtoDispensados(Integer prontuario,
			String local, String paciente, String prescricao_inicio,
			String prescricao_fim) throws ApplicationBusinessException {
		
		Integer qtdCharsLinha = 45;
		
		StringBuffer matricial = new StringBuffer(765);
		
		/*Linha em branco*/ matricial.append(completaComCaracter(qtdCharsLinha, ' ')).append('\n');
		
		AghParametros parametroRazaoSocial = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
		String nomeHosp = StringUtil.subtituiAcentos(parametroRazaoSocial.getVlrTexto());
		
		if(nomeHosp.length() > 45){
			matricial.append(nomeHosp.subSequence(0, 45)).append('\n')
			.append(nomeHosp.subSequence(45, nomeHosp.length())).append('\n');
		}else{
			/*Nome do Hospital*/ 
		    matricial.append(nomeHosp).append('\n');
		}
		
		/*Linha em branco*/ 
		matricial.append(completaComCaracter(qtdCharsLinha, ' ')).append('\n')
		
		/* Hifen */	
		.append(completaComCaracter(qtdCharsLinha, '-')).append('\n')
		//Prontuario
		.append("Prontuario: ").append(prontuario.toString()).append('\n')
		//Localizacao
		.append("Localizacao: ").append(local).append('\n')
		//Paciente
		.append("Paciente: ");
		if(paciente.length() > 45){
			matricial.append(paciente.subSequence(0, 45)).append('\n');
			matricial.append(paciente.subSequence(45, paciente.length())).append('\n');
		}else{
			matricial.append(paciente).append('\n');
		}
		
		//Prescricao
		matricial.append("Prescricao: ").append(prescricao_inicio).append(" a ").append(prescricao_fim).append('\n')
		
		/*Linha em branco*/ 
		.append(completaComCaracter(qtdCharsLinha, ' ')).append('\n');
		
		/*Integer qtdLinhasEmBranco = 12;
		for(int i = 0; i< qtdLinhasEmBranco ; i++){
			matricial.append(completaComCaracter(45, ' '));
			matricial.append('\n');
		}*/
		
		return matricial;
	}
	
	public String completaComCaracter(Integer qtde, Character caracter) {
        StringBuilder retorno = new StringBuilder("");
        for (int i=0;i<qtde;i++){
             retorno.append(caracter);
        }
        return retorno.toString();
	}
	
	public void atualizarRegistroImpressao(List<TicketMdtoDispensadoVO> listaMdtoDispensado, RapServidores servidorLogado){
		for (TicketMdtoDispensadoVO ticketMdtoDispensadoVO : listaMdtoDispensado) {
			AfaDispensacaoMdtos dispensacaoMdto = getAfaDispensacaoMdtosDAO()
					.obterPorChavePrimaria(ticketMdtoDispensadoVO.getDispensacaoMdtoSeq());
			if(ticketMdtoDispensadoVO.getCheckboxReadonly()){
				dispensacaoMdto.setDthrTicket(new Date());
				dispensacaoMdto.setServidorTicket(servidorLogado);
				getAfaDispensacaoMdtosDAO().atualizar(dispensacaoMdto);
			}
		}
	}
	
	protected AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO(){
		return afaDispensacaoMdtosDAO;
	}
	
}
